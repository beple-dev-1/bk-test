package com.example.bkpaymenttest.controller.api;

import com.example.bkpaymenttest.dto.common.ApiResponse;
import com.example.bkpaymenttest.dto.common.BpayCallResult;
import com.example.bkpaymenttest.dto.member.MemberCheckDetailResponse;
import com.example.bkpaymenttest.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountApiController {

    private final AccountService accountService;

    @PostMapping("/check")
    public ResponseEntity<ApiResponse<BpayCallResult<MemberCheckDetailResponse>>> check(
            @RequestBody Map<String, String> body) {
        String uuid = body.get("uuid");
        BpayCallResult<MemberCheckDetailResponse> result = accountService.checkMember(uuid);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
