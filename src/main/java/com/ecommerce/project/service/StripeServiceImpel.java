package com.ecommerce.project.service;

import com.ecommerce.project.Payload.StripePaymentDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StripeServiceImpel implements  StripeService{



    @Value("${STRIPE_SECRET_KEY}")
    private String stripeApiKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey=stripeApiKey;
    }

    @Override
    public PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDTO) throws StripeException {


        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(stripePaymentDTO.getAmount())
                        .setCurrency(stripePaymentDTO.getCurrency())
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        PaymentIntent paymentIntent =  PaymentIntent.create(params);
        return paymentIntent;
    }

    @Override
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }
}
