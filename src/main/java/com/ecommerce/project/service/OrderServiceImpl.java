package com.ecommerce.project.service;

import com.ecommerce.project.Payload.OrderDTO;
import com.ecommerce.project.Payload.OrderItemDTO;
import com.ecommerce.project.Repositories.*;
import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private final CartService cartService;

    @Transactional
    @Override
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {


        Cart cart =cartRepository.findCartByEmail(emailId);
        if(cart==null){
            throw new ResourceNotFoundException("Cart","email",emailId);
        }
        Address address=addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("Address","AddressId",addressId));

        Order order =new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted!");
        order.setAddress(address);


        Payment payment =new Payment(paymentMethod,pgPaymentId,pgStatus,pgResponseMessage,pgName);
        payment.setOrders(order);
        payment=paymentRepository.save(payment);
        order.setPayment(payment);

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
}
