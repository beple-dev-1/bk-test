package com.example.bkpaymenttest.common.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256 GCM 암복호화 유틸리티
 *
 * 규칙: 암복호화 처리는 반드시 이 클래스에서만 수행한다.
 * 서비스 레이어에서 직접 암복호화 로직을 작성하지 않는다.
 */
public class CryptoUtils {

    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ALGORITHM = "AES";

    private CryptoUtils() {}

    /**
     * AES-256 GCM 암호화
     * - 랜덤 IV(12바이트) 생성
     * - IV + 암호문 결합 후 Base64 인코딩
     *
     * @param plaintext 평문
     * @param key       AES 키 (16/24/32 바이트 문자열)
     * @return Base64(IV + 암호문)
     */
    public static String encrypt(String plaintext, String key) throws Exception {
        if (plaintext == null || key == null || plaintext.isEmpty() || key.isEmpty()) {
            throw new IllegalArgumentException("Plain text or key is empty.");
        }

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        validateKeyLength(keyBytes);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

        // 랜덤 IV 생성 (12바이트)
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

        byte[] encText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // IV + 암호문 결합
        byte[] combined = new byte[iv.length + encText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encText, 0, combined, iv.length, encText.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * AES-256 GCM 복호화
     * - Base64 디코딩
     * - 앞 12바이트 = IV, 나머지 = 암호문
     *
     * @param base64Ciphertext Base64(IV + 암호문)
     * @param key              AES 키 (16/24/32 바이트 문자열)
     * @return 복호화된 평문
     */
    public static String decrypt(String base64Ciphertext, String key) throws Exception {
        if (base64Ciphertext == null || key == null || base64Ciphertext.isEmpty() || key.isEmpty()) {
            throw new IllegalArgumentException("Cipher text or key is empty.");
        }

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        validateKeyLength(keyBytes);

        byte[] combined = Base64.getDecoder().decode(base64Ciphertext);

        // IV 추출 (앞 12바이트)
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);

        // 암호문 추출
        byte[] cipherText = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, GCM_IV_LENGTH, cipherText, 0, cipherText.length);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

        byte[] decrypted = cipher.doFinal(cipherText);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private static void validateKeyLength(byte[] keyBytes) {
        int len = keyBytes.length;
        if (len != 16 && len != 24 && len != 32) {
            throw new IllegalArgumentException("Key length must be 16, 24, or 32 bytes. actual=" + len);
        }
    }
}
