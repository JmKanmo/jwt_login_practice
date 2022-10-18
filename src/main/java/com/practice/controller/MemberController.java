package com.practice.controller;

import com.practice.config.jwt.TokenProvider;
import com.practice.domain.Member;
import com.practice.model.LoginModel;
import com.practice.model.MemberModel;
import com.practice.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberServiceImpl memberServiceImpl;
    private final TokenProvider tokenProvider;

    // 요청 방식은 JSON, WWW3-unicode 등등 상관없음.
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberModel memberModel) {
        var result = this.memberServiceImpl.register(memberModel);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> signin(@RequestBody LoginModel loginModel) {
        Member member = this.memberServiceImpl.authenticate(loginModel);
        String token = this.tokenProvider.generateToken(member.getUsername(), new ArrayList<>());
        return ResponseEntity.ok(token);
    }
}
