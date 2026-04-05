package com.example.bkpaymenttest.controller.api;

import com.example.bkpaymenttest.dto.common.ApiResponse;
import com.example.bkpaymenttest.dto.common.BpayCallResult;
import com.example.bkpaymenttest.dto.settle.SettleSummaryDetailResponse;
import com.example.bkpaymenttest.service.reconciliation.ReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settle")
@RequiredArgsConstructor
public class SettleApiController {

    private final ReconciliationService reconciliationService;

    @PostMapping("/summary")
    public ResponseEntity<ApiResponse<BpayCallResult<SettleSummaryDetailResponse>>> settleSummary(
            @RequestBody Map<String, String> body) {
        String strDate = body.get("strDate");
        String endDate = body.get("endDate");
        BpayCallResult<SettleSummaryDetailResponse> result = reconciliationService.settleSummary(strDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
