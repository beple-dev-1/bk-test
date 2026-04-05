package com.example.bkpaymenttest.dto.reverse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CpmCompleteDetailRequest {

    @JsonProperty("UUID")
    private String uuid;

    /** QR/바코드 생성 API 호출 시 사용한 TNO */
    @JsonProperty("ORG_TNO")
    private String orgTno;

    @JsonProperty("PAY_INFO")
    private CpmCompletePayInfo payInfo;
}
