package com.example.bkpaymenttest.service.reverse;

import com.example.bkpaymenttest.dto.reverse.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 비플페이 → 이용기관 수신 API 비즈니스 로직
 * 암복호화는 컨트롤러 레이어(TestReverseApiController / InboundApiController)에서만 처리
 */
@Slf4j
@Service
public class ReverseApiService {

    /**
     * 환불결과 통지 — POST /api/bpy/v1/refund/noti
     */
    public RefundNotiDetailResponse processRefundNoti(RefundNotiDetailRequest req) {
        log.info("[ReverseApiService] refundNoti uuid={}, trgtTrxDt={}, amt={}",
                req.getUuid(), req.getTrgtTrxDt(), req.getAmt());
        return RefundNotiDetailResponse.builder()
                .resCd("0000")
                .resMsg("정상")
                .build();
    }

    /**
     * 포인트 거래결과 조회 — POST /api/bpy/v1/tran/result
     */
    public TranResultDetailResponse processTranResult(TranResultDetailRequest req) {
        log.info("[ReverseApiService] tranResult uuid={}, type={}, trgtTrxDt={}",
                req.getUuid(), req.getType(), req.getTrgtTrxDt());
        return TranResultDetailResponse.builder()
                .resCd("0000")
                .resMsg("정상")
                .build();
    }

    /**
     * 포인트 차감/환불/망취소 요청 — POST /api/bpy/v1/pay/request
     */
    public PayRequestDetailResponse processPayRequest(PayRequestDetailRequest req) {
        log.info("[ReverseApiService] payRequest uuid={}, type={}, amt={}, afltId={}",
                req.getUuid(), req.getType(), req.getAmt(), req.getAfltId());
        Long amt = null;
        try { amt = Long.parseLong(req.getAmt()); } catch (Exception ignored) {}
        return PayRequestDetailResponse.builder()
                .resCd("0000")
                .resMsg("정상")
                .amt(amt)
                .build();
    }

    /**
     * CPM 결제완료 통지 — POST /api/bpy/v1/cpm/complete
     */
    public CpmCompleteDetailResponse processCpmComplete(CpmCompleteDetailRequest req) {
        log.info("[ReverseApiService] cpmComplete uuid={}, orgTno={}",
                req.getUuid(), req.getOrgTno());
        return CpmCompleteDetailResponse.builder()
                .resCd("0000")
                .resMsg("정상")
                .build();
    }
}
