package com.practice.controller.global;

import com.practice.exception.message.ExceptionMessage;
import com.practice.exception.model.UserAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/global")
@RequiredArgsConstructor
public class GlobalRestController {
    @PostMapping("/trade/request")
    public ResponseEntity<?> trade(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new UserAuthException(ExceptionMessage.NOT_AUTHORIZED_ACCESS);
        }
        return ResponseEntity.ok(String.format("트레이드 신청 결과: %d", new Random().nextInt()));
    }

    @GetMapping("/trade/inquire")
    public ResponseEntity<?> getTrade(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new UserAuthException(ExceptionMessage.NOT_AUTHORIZED_ACCESS);
        }
        return ResponseEntity.ok(String.format("어서오세요, Jm트레이드 센터입니다. 현재 트레이드 지수는 %d 입니다.", 25555));
    }
}
