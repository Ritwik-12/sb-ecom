package com.ecommerce.project.controller;

import com.ecommerce.project.Repositories.PaymentRepository;
import com.ecommerce.project.model.Payment;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/webhook")
public class RazorpayWebhookController {

    private final PaymentRepository paymentRepository;

    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleRazorpayWebhook(HttpServletRequest request) throws IOException {

        // The signature covers the exact raw bytes, so read the body ourselves.
        String payload = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String signature = request.getHeader("X-Razorpay-Signature");

        if (signature == null) {
            return ResponseEntity.badRequest().body("Missing signature");
        }

        try {
            if (!Utils.verifyWebhookSignature(payload, signature, webhookSecret)) {
                log.warn("Invalid Razorpay webhook signature");
                return ResponseEntity.badRequest().body("Invalid signature");
            }
        } catch (RazorpayException e) {
            log.error("Razorpay webhook signature check failed", e);
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        JSONObject json = new JSONObject(payload);
        String event = json.optString("event");

        switch (event) {
            case "payment.captured" -> handlePayment(json, "captured");
            case "payment.failed"   -> handlePayment(json, "failed");
            default -> log.info("Unhandled Razorpay event: {}", event);
        }
        return ResponseEntity.ok("Received");
    }

    private void handlePayment(JSONObject json, String status) {
        JSONObject entity = json.getJSONObject("payload")
                .getJSONObject("payment").getJSONObject("entity");
        String paymentId = entity.getString("id");
        String orderId = entity.optString("order_id");

        if ("failed".equals(status)) {
            // A failed attempt never has a Payment row (rows exist only after success).
            log.info("Razorpay payment {} failed for order {}", paymentId, orderId);
            return;
        }

        Optional<Payment> existing = paymentRepository.findByPgPaymentId(paymentId);
        if (existing.isPresent()) {
            Payment payment = existing.get();
            payment.setPgStatus("captured");
            paymentRepository.save(payment);
            log.info("Payment {} confirmed captured by webhook", paymentId);
        } else {
            // Often just timing: the webhook can arrive a moment before placeOrder finishes.
            log.warn("Captured payment {} (order {}) has no Payment record yet", paymentId, orderId);
        }
    }
}