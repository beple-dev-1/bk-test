package com.example.bkpaymenttest.dto.common;

import lombok.Builder;
import lombok.Getter;

/**
 * 비플페이 API 호출 결과 — 우측 개발자 로그 패널 표시용
 */
@Getter
@Builder
public class BpayCallResult<T> {

    private final String apiName;
    private final String url;

    // 요청 정보
    private final String requestPlainJson;
    private final String requestEncryptedData;

    // 응답 정보
    private final String responseEncryptedData;
    private final String responsePlainJson;

    // 응답 COMM
    private final String rc;
    private final String rm;
    private final String resDttm;
    private final String tno;

    // 응답 DETAIL
    private final T detail;

    // 오류 발생 시 상세 내용 (HTTP 에러 바디 또는 예외 메시지)
    private final String errorDetail;
}
