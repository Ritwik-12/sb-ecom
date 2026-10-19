package com.ecommerce.project.service;

import com.ecommerce.project.Payload.RazorpayOrderResponse;
import com.ecommerce.project.Repositories.CartRepository;
import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.model.Cart;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayServiceImpl {

    private final RazorpayClient client;
    private final CartRepository cartRepository;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    public RazorpayOrderResponse createOrderForCart(String email) {
        Cart cart = cartRepository.findCartByEmail(email);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new ApiException("Cart is empty!");
        }

        long amountInPaise = Math.round(cart.getTotalPrice() * 100);

        try {
            JSONObject req = new JSONObject();
            req.put("amount", amountInPaise);
            req.put("currency", "INR");
            req.put("receipt", "cart_" + cart.getId() + "_" + System.currentTimeMillis());

            com.razorpay.Order rzpOrder = client.orders.create(req); // not your model.Order
            return new RazorpayOrderResponse(keyId, rzpOrder.get("id"), amountInPaise, "INR");
        } catch (RazorpayException e) {
            throw new ApiException("Unable to create Razorpay order");
        }
    }



    public String verifyPayment(String rzpOrderId, String rzpPaymentId,
                                String signature, long expectedPaise) {

        if (!StringUtils.hasText(rzpOrderId) || !StringUtils.hasText(rzpPaymentId)
                || !StringUtils.hasText(signature)) {
            throw new ApiException("Missing Razorpay payment details.");
        }

        try {
            JSONObject attrs = new JSONObject();
            attrs.put("razorpay_order_id", rzpOrderId);
            attrs.put("razorpay_payment_id", rzpPaymentId);
            attrs.put("razorpay_signature", signature);

            if (!Utils.verifyPaymentSignature(attrs, keySecret)) {
                throw new ApiException("Invalid payment signature.");
            }

            com.razorpay.Payment payment = client.payments.fetch(rzpPaymentId);
            String status = payment.get("status");
            String paidOrderId = payment.get("order_id");
            Number paidAmount = payment.get("amount");

            if (!rzpOrderId.equals(paidOrderId)) {
                throw new ApiException("Payment does not belong to this Razorpay order.");
            }
            if (!"captured".equals(status)) {
                throw new ApiException("Payment has not been completed. Current status: " + status);
            }
            if (paidAmount == null || paidAmount.longValue() != expectedPaise) {
                log.error("Amount mismatch for {}: expected {} got {}", rzpPaymentId, expectedPaise, paidAmount);
                throw new ApiException("Payment amount does not match order total.");
            }
            return status;

        } catch (RazorpayException e) {
            log.error("Razorpay verification failed for payment {}", rzpPaymentId, e);
            throw new ApiException("Unable to verify payment with Razorpay.");
        }
    }
}
