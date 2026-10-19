package com.ecommerce.project.Payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RazorpayOrderResponse {

    private String keyId;
    private String razorpayOrderId;
    private Long amount;      // paise
    private String currency;
}
