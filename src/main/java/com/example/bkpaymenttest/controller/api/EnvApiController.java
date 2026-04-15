package com.example.bkpaymenttest.controller.api;

import com.example.bkpaymenttest.config.EnvConfig;
import com.example.bkpaymenttest.dto.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 개발/운영 환경 전환 API.
 * UI 토글에서 POST /api/env { "env": "prod" | "dev" } 를 호출한다.
 */
@RestController
@RequestMapping("/api/env")
@RequiredArgsConstructor
public class EnvApiController {

    private final EnvConfig envConfig;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> setEnv(@RequestBody Map<String, String> body) {
        boolean isProd = "prod".equals(body.get("env"));
        envConfig.setProd(isProd);
        return ResponseEntity.ok(ApiResponse.success(buildPayload()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> getEnv() {
        return ResponseEntity.ok(ApiResponse.success(buildPayload()));
    }

    private Map<String, String> buildPayload() {
        return Map.of(
                "env",     envConfig.getEnvKey(),
                "label",   envConfig.getEnvName(),
                "baseUrl", envConfig.getBaseUrl()
        );
    }
}
