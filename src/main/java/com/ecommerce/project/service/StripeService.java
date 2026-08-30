package com.ecommerce.project.service;

import com.ecommerce.project.Payload.StripePaymentDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeService {

    PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDTO) throws StripeException;
}
