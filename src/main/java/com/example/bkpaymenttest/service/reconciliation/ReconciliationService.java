package com.example.bkpaymenttest.service.reconciliation;

import com.example.bkpaymenttest.client.bpay.BpayClient;
import com.example.bkpaymenttest.dto.common.BpayCallResult;
import com.example.bkpaymenttest.dto.settle.SettleSummaryDetailRequest;
import com.example.bkpaymenttest.dto.settle.SettleSummaryDetailResponse;
import com.example.bkpaymenttest.dto.settle.SummaryInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReconciliationService {

    private final BpayClient bpayClient;

    /**
     * 결제내역 집계 조회 — POST /bravo/v1/settle/summary
     * ORG_SUMMARY_INFO는 테스트 앱이므로 0으로 전송
     */
    public BpayCallResult<SettleSummaryDetailResponse> settleSummary(String strDate, String endDate) {
        log.info("[ReconciliationService] settleSummary strDate={}, endDate={}", strDate, endDate);

        SummaryInfo orgSummaryInfo = SummaryInfo.builder()
                .totPayCnt("0")
                .totPayAmt("0")
                .totRfndCnt("0")
                .totRfndAmt("0")
                .totCnclCnt("0")
                .totCnclAmt("0")
                .totRfndCnclCnt("0")
                .totRfndCnclAmt("0")
                .build();

        SettleSummaryDetailRequest detail = SettleSummaryDetailRequest.builder()
                .strDate(strDate)
                .endDate(endDate)
                .orgSummaryInfo(orgSummaryInfo)
                .build();

        return bpayClient.call("결제내역 집계 조회", "/bravo/v1/settle/summary", detail, SettleSummaryDetailResponse.class);
    }
}
