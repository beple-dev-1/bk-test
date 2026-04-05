package com.example.bkpaymenttest.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {

    // TODO: add payment request fields
    private String accountNumber;
    private String amount;
    private String currency;
}
