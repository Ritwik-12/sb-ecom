package com.ecommerce.project.controller;


import com.ecommerce.project.Payload.OrderDTO;
import com.ecommerce.project.Payload.OrderRequestDTO;
import com.ecommerce.project.Payload.RazorpayOrderResponse;
import com.ecommerce.project.Payload.StripePaymentDTO;
import com.ecommerce.project.service.OrderService;
import com.ecommerce.project.service.RazorpayServiceImpl;
import com.ecommerce.project.service.StripeService;
import com.ecommerce.project.util.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class OrderController {


    private final OrderService orderService;
    private  final AuthUtil authUtil;
    private final StripeService stripeService;
    private final RazorpayServiceImpl razorpayService;


    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(
            @PathVariable  String paymentMethod,
           @RequestBody OrderRequestDTO orderRequestDTO
    ){

        String emailId=authUtil.loggedInEmail();
         OrderDTO order=   orderService.placeOrder(
                    emailId,
                    orderRequestDTO.getAddressId(),
                    paymentMethod,
                    orderRequestDTO.getPgName(),
                    orderRequestDTO.getPgPaymentId(),
                    orderRequestDTO.getPgStatus(),
                    orderRequestDTO.getPgResponseMessage()
            );

         return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/order/stripe-client-secret")
    public ResponseEntity<String> createStripePaymentSecret(@RequestBody(required = false) StripePaymentDTO stripePaymentDTO ) throws StripeException {
        String emailId = authUtil.loggedInEmail();
        long amountInPaise = orderService.validateCheckoutAndGetAmountInPaise(emailId);

        StripePaymentDTO serverSide = new StripePaymentDTO(amountInPaise, "inr");
        PaymentIntent paymentIntent = stripeService.paymentIntent(serverSide);
        return new ResponseEntity<>(paymentIntent.getClientSecret(), HttpStatus.CREATED);
    }




    @PostMapping("/order/razorpay/create")
    public ResponseEntity<RazorpayOrderResponse> createRazorpayOrder() {
        return ResponseEntity.ok(razorpayService.createOrderForCart(authUtil.loggedInEmail()));
    }
}
