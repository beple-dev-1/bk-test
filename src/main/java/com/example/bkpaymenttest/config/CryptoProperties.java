package com.example.bkpaymenttest.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CryptoProperties {

    @Value("${ENCRYPT_KEY}")
    private String encryptKey;
}
