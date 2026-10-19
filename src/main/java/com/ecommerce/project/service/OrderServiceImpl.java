package com.ecommerce.project.service;

import com.ecommerce.project.Payload.OrderDTO;
import com.ecommerce.project.Payload.OrderItemDTO;
import com.ecommerce.project.Repositories.*;
import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    private final RazorpayServiceImpl razorpayService;


    private final CartService cartService;

    private final StripeService stripeService;



    private static final Set<String> ONLINE_PAYMENT_METHODS= Set.of("ONLINE","STRIPE","CARD");


    @Transactional
    @Override
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName,
                               String pgPaymentId, String pgStatus, String pgResponseMessage,
    String pgOrderId, String pgSignature) {


        Cart cart =cartRepository.findCartByEmail(emailId);
        if(cart==null){
            throw new ResourceNotFoundException("Cart","email",emailId);
        }
        Address address=addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("Address","AddressId",addressId));



        String verifiedStatus = pgStatus;
        String method = paymentMethod == null ? "" : paymentMethod.toUpperCase();
        long expectedAmount = Math.round(cart.getTotalPrice() * 100);

        if ("RAZORPAY".equals(method)) {
            requireUnusedPayment(pgPaymentId);
            verifiedStatus = razorpayService.verifyPayment(pgOrderId, pgPaymentId, pgSignature, expectedAmount);
            pgName = "Razorpay";                       // set server-side, not from the client

        } else if (ONLINE_PAYMENT_METHODS.contains(method)) {
            requireUnusedPayment(pgPaymentId);
            // ... your existing Stripe block, unchanged ...
        }


        Order order =new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted!");
        order.setAddress(address);


        Payment payment = new Payment(paymentMethod, pgPaymentId, verifiedStatus, pgResponseMessage, pgName);
        payment.setPgOrderId(pgOrderId);
        payment.setOrders(order);

        Order savedOrder=orderRepository.save(order);
        List<CartItem> cartItems=cart.getItems();
        if(cartItems.isEmpty()){
            throw new ApiException("Cart is empty!");
        }
        // new validation for cart

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();
            int requestedQuantity = cartItem.getQuantity();
            int availableQuantity = product.getQuantity();

            if (availableQuantity <= 0) {
                throw new ApiException(
                        product.getProductName() + " is currently out of stock."
                );
            }

            if (requestedQuantity > availableQuantity) {
                throw new ApiException(
                        "Only " + availableQuantity +
                                " units of " + product.getProductName() +
                                " are currently available."
                );
            }
        }

        List<OrderItem> orderItems=new ArrayList<>();
        for(CartItem cartItem:cartItems){
            OrderItem orderItem=new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());

            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }
          orderItems= orderItemRepository.saveAll(orderItems);

        cart.getItems().forEach(item->{
            int quantity=item.getQuantity();
            Product product=item.getProduct();
            product.setQuantity(product.getQuantity()-quantity);
            productRepository.save(product);

            cartService.deleteProductFromCart(cart.getId(),item.getProduct().getProductId());

        });


        OrderDTO orderDTO=modelMapper.map(savedOrder,OrderDTO.class);
        orderItems.forEach(item->
                orderDTO.getOrderItems().add(
                        modelMapper.map(item, OrderItemDTO.class)
                ));

        orderDTO.setAddressId(addressId);
        return orderDTO;
    }

    private void requireUnusedPayment(String pgPaymentId) {
        if (pgPaymentId != null && paymentRepository.findByPgPaymentId(pgPaymentId).isPresent()) {
            throw new ApiException("This payment has already been used for an order.");
        }
    }


}
