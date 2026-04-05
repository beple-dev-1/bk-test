package com.example.bkpaymenttest.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QrCreateDetailResponse {

    /** CPM 결제용 QR 코드 — 반드시 이미지로 렌더링 */
    @JsonProperty("QR_CODE")
    private String qrCode;

    /** 바코드 앞자리 39 — 화면에 렌더링하지 않음 */
    @JsonProperty("BAR_CODE")
    private String barCode;

    /** 만료일시 yyyymmddhh24miss, 유효시간 170초 */
    @JsonProperty("EXP_TIME")
    private String expTime;
}
