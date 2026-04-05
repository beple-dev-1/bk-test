package com.example.bkpaymenttest.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageCode {

    SUCCESS("0000", "Success"),
    UNKNOWN_ERROR("9999", "Unknown error occurred"),

    // Crypto
    ENCRYPT_FAILED("2001", "Encryption failed"),
    DECRYPT_FAILED("2002", "Decryption failed"),

    // Payment
    PAYMENT_FAILED("1001", "Payment failed"),
    INVALID_ACCOUNT("1002", "Invalid account number");

    private final String code;
    private final String message;
}
