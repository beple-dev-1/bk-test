package com.example.bkpaymenttest.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Component
@PropertySource(value = "classpath:crypto.properties", ignoreResourceNotFound = true)
public class CryptoProperties {

    // 로컬: crypto.properties 의 encryptKey / Railway: ENCRYPT_KEY 환경변수
    @Value("${encryptKey:${ENCRYPT_KEY:}}")
    private String encryptKey;

    @Value("${prodEncryptKey:${PROD_ENCRYPT_KEY:}}")
    private String prodEncryptKey;

    public String getKeyFor(boolean isProd) {
        return isProd ? prodEncryptKey : encryptKey;
    }
}
