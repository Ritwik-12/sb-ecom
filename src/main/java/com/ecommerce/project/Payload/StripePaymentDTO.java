package com.ecommerce.project.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StripePaymentDTO {

    private Long amount;
    private String currency;

}
