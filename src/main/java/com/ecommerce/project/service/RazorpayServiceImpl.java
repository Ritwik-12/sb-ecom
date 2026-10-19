package com.ecommerce.project.service;

import com.ecommerce.project.Payload.RazorpayOrderResponse;
import com.ecommerce.project.Repositories.CartRepository;
import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.model.Cart;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl {

    private final RazorpayClient client;
    private final CartRepository cartRepository;

    @Value("${razorpay.key-id}")
    private String keyId;

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
}
