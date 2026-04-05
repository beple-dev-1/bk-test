package com.example.bkpaymenttest.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Component
@PropertySource("classpath:crypto.properties")
public class CryptoProperties {

    @Value("${encryptKey}")
    private String encryptKey;
}
