package com.example.bkpaymenttest.controller.api;

import com.example.bkpaymenttest.dto.common.ApiResponse;
import com.example.bkpaymenttest.dto.common.BpayCallResult;
import com.example.bkpaymenttest.dto.member.MemberCheckDetailResponse;
import com.example.bkpaymenttest.dto.member.MemberJoinDetailRequest;
import com.example.bkpaymenttest.dto.member.MemberJoinDetailResponse;
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

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<BpayCallResult<MemberJoinDetailResponse>>> join(
            @RequestBody Map<String, String> body) {
        MemberJoinDetailRequest detail = MemberJoinDetailRequest.builder()
                .uuid(body.get("uuid"))
                .type(body.get("type"))
                .ci(nullIfBlank(body.get("ci")))
                .mobNo(nullIfBlank(body.get("mobNo")))
                .membNm(nullIfBlank(body.get("membNm")))
                .brtDt(nullIfBlank(body.get("brtDt")))
                .gndr(nullIfBlank(body.get("gndr")))
                .inFrnTp(nullIfBlank(body.get("inFrnTp")))
                .build();
        BpayCallResult<MemberJoinDetailResponse> result = accountService.joinMember(detail);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/check")
    public ResponseEntity<ApiResponse<BpayCallResult<MemberCheckDetailResponse>>> check(
            @RequestBody Map<String, String> body) {
        String uuid = body.get("uuid");
        BpayCallResult<MemberCheckDetailResponse> result = accountService.checkMember(uuid);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    private String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
