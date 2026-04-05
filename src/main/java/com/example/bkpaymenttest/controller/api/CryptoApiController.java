package com.example.bkpaymenttest.controller.api;

import com.example.bkpaymenttest.dto.common.ApiResponse;
import com.example.bkpaymenttest.service.crypto.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoApiController {

    private final CryptoService cryptoService;

    @PostMapping("/encrypt")
    public ResponseEntity<ApiResponse<Map<String, String>>> encrypt(@RequestBody Map<String, String> body) {
        String plaintext = body.get("plaintext");
        String encrypted = cryptoService.encrypt(plaintext);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "plaintext", plaintext,
                "encrypted", encrypted
        )));
    }

    @PostMapping("/decrypt")
    public ResponseEntity<ApiResponse<Map<String, String>>> decrypt(@RequestBody Map<String, String> body) {
        String ciphertext = body.get("ciphertext");
        String decrypted = cryptoService.decrypt(ciphertext);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "ciphertext", ciphertext,
                "decrypted", decrypted
        )));
    }
}
