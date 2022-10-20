package com.practice.controller.member;

import com.practice.dto.JwtTokenDto;
import com.practice.exception.message.ExceptionMessage;
import com.practice.exception.model.UserAuthException;
import com.practice.service.member.MemberService;
import com.practice.model.LoginModel;
import com.practice.model.MemberModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberRestController {
    private final MemberService memberService;

    @GetMapping("/info")
    public ResponseEntity<?> info(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new UserAuthException(ExceptionMessage.NOT_AUTHORIZED_ACCESS);
        }
        return ResponseEntity.status(HttpStatus.OK).body(memberService.findMemberById(principal.getName()));
    }

    // 요청 방식은 Content-Type 상관없이 ...
    @PostMapping("/signup")
    public ResponseEntity<?> signup(MemberModel memberModel) {
        var result = this.memberService.register(memberModel);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/authorize")
    public ResponseEntity<?> authorize(@RequestHeader("Authorization") String accessToken, Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new UserAuthException(ExceptionMessage.NOT_AUTHORIZED_ACCESS);
        }
        return ResponseEntity.ok(JwtTokenDto.from(accessToken));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(LoginModel loginModel) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.login(loginModel));
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String accessToken) {
        memberService.logout(accessToken);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue("RefreshToken") String refreshToken, Principal principal) {
        return ResponseEntity.ok(memberService.reissue(refreshToken, principal));
    }
}
