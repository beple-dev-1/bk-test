package com.example.bkpaymenttest.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비플페이 API 공통 HTTP 요청/응답 바디
 * { "DATA": "BASE64_AESGCM_ENCRYPTED_STRING" }
 *
 * ignoreUnknown = true: 서버가 DATA 외 추가 필드(COMMON_HEAD 등)를 반환해도 무시
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataEnvelope {

    @JsonProperty("DATA")
    private String data;
}
