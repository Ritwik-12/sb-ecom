package com.ecommerce.project.controller;


import com.ecommerce.project.Repositories.PaymentRepository;
import com.ecommerce.project.model.Payment;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/webhook")
public class StripeWebhookController {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;



    public ResponseEntity<String> handleStripeWebhook(
        HttpServletRequest request) throws IOException {

        String payload=new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String sigHeader= request.getHeader("Stripe-Signature");

        Event event;

        try{
            event= Webhook.constructEvent(payload,sigHeader,webhookSecret);
        }catch(SignatureVerificationException e){
            log.warn("invalid stripe webhook signature",e);
            return new ResponseEntity<>("Invalid signature", HttpStatus.BAD_REQUEST);
        }


        switch(event.getType()){
            case "payment_intent.succeeded" -> handlePaymentIntentEvent(event, "succeeded");
            case "payment_intent.payment_failed" ->handlePaymentIntentEvent(event,"failed");
                default -> log.info("Unhandled Stripe event type: {}", event.getType());
        }

        return new ResponseEntity<>("Received", HttpStatus.OK);

    }

    private void handlePaymentIntentEvent(Event event, String resolvedStatus) {
        Optional<StripeObject> stripeObject = event.getDataObjectDeserializer().getObject();

        if (stripeObject.isEmpty() || !(stripeObject.get() instanceof PaymentIntent paymentIntent)) {
            log.warn("Could not deserialize PaymentIntent for event {}", event.getId());
            return;
        }

        String paymentIntentId = paymentIntent.getId();

        // If placeOrder() already ran and created the Payment row, keep it in sync.
        // If it hasn't run yet (e.g. the browser never came back), there is nothing
        // to reconcile yet - the order simply doesn't exist. Logging here at least
        // gives you visibility into "money received but no matching order" cases,
        // which you can alert on or reconcile manually/via a scheduled job.
        Optional<Payment> existing = paymentRepository.findByPgPaymentId(paymentIntentId);

        if (existing.isPresent()) {
            Payment payment = existing.get();
            payment.setPgStatus(resolvedStatus);
            paymentRepository.save(payment);
            log.info("Updated Payment {} for PaymentIntent {} to status {}",
                    payment.getPaymentId(), paymentIntentId, resolvedStatus);
        } else {
            log.warn("Received {} webhook for PaymentIntent {} with no matching Payment record yet.",
                    resolvedStatus, paymentIntentId);
        }
    }

}
