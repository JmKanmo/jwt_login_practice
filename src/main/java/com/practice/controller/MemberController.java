package com.practice.controller;

import com.practice.domain.token.RefreshToken;
import com.practice.dto.TokenDto;
import com.practice.service.MemberService;
import com.practice.service.RefreshTokenService;
import com.practice.util.JwtTokenUtil;
import com.practice.domain.Member;
import com.practice.model.LoginModel;
import com.practice.model.MemberModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 요청 방식은 Content-Type 상관없이 ...
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberModel memberModel) {
        var result = this.memberService.register(memberModel);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginModel loginModel) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.login(loginModel));
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String accessToken) {
        memberService.logout(accessToken);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestHeader("RefreshToken") String refreshToken, Principal principal) {
        return ResponseEntity.ok(memberService.reissue(refreshToken, principal));
    }
}
