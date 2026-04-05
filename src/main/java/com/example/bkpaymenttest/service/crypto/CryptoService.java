package com.example.bkpaymenttest.service.crypto;

import com.example.bkpaymenttest.common.crypto.CryptoUtils;
import com.example.bkpaymenttest.common.exception.BusinessException;
import com.example.bkpaymenttest.common.message.MessageCode;
import com.example.bkpaymenttest.config.CryptoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 암복호화 서비스
 *
 * 주의: 이 클래스는 CryptoUtils 호출만 담당한다.
 * 암복호화 로직은 반드시 common/crypto/CryptoUtils 에서만 처리한다. (CLAUDE.md 규칙)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoService {

    private final CryptoProperties cryptoProperties;

    public String encrypt(String plaintext) {
        try {
            return CryptoUtils.encrypt(plaintext, cryptoProperties.getEncryptKey());
        } catch (Exception e) {
            log.error("[CryptoService] encrypt failed: {}", e.getMessage());
            throw new BusinessException(MessageCode.ENCRYPT_FAILED);
        }
    }

    public String decrypt(String ciphertext) {
        try {
            return CryptoUtils.decrypt(ciphertext, cryptoProperties.getEncryptKey());
        } catch (Exception e) {
            log.error("[CryptoService] decrypt failed: {}", e.getMessage());
            throw new BusinessException(MessageCode.DECRYPT_FAILED);
        }
    }
}
