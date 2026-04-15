package com.example.bkpaymenttest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 런타임 환경(개발/운영) 상태 관리.
 * UI 토글에 의해 setProd()가 호출되면 이후 모든 API 호출에 즉시 반영된다.
 *
 * 초기값: 환경변수 DEFAULT_ENV=prod 로 설정하면 앱 시작 시 운영으로 기동.
 * 미설정 시 기본값은 개발(dev).
 */
@Component
public class EnvConfig {

    @Value("${bpay.dev.base-url}")
    private String devBaseUrl;

    @Value("${bpay.prod.base-url}")
    private String prodBaseUrl;

    @Value("${bpay.dev.reverse-base-url}")
    private String devReverseBaseUrl;

    @Value("${bpay.prod.reverse-base-url}")
    private String prodReverseBaseUrl;

    private volatile boolean prod;

    public EnvConfig(@Value("${DEFAULT_ENV:dev}") String defaultEnv) {
        this.prod = "prod".equalsIgnoreCase(defaultEnv);
    }

    public boolean isProd() { return prod; }

    public void setProd(boolean prod) { this.prod = prod; }

    public String getEnvKey()  { return prod ? "prod" : "dev"; }
    public String getEnvName() { return prod ? "운영" : "개발"; }

    /** 시나리오 API (이용기관 → 비플페이) 베이스 URL */
    public String getBaseUrl() { return prod ? prodBaseUrl : devBaseUrl; }

    /** 역거래 API (비플페이 → 이용기관) 목적지 베이스 URL */
    public String getReverseBaseUrl() { return prod ? prodReverseBaseUrl : devReverseBaseUrl; }

    /** 역거래 테스트 게이트웨이 URL */
    public String getGatewayUrl() {
        return getBaseUrl() + "/TEST_GATEWAY_040.jct";
    }
}
