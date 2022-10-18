package com.practice.controller;

import com.practice.exception.message.ExceptionMessage;
import com.practice.exception.model.UserAuthException;
import com.practice.model.MemberModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/global")
@RequiredArgsConstructor
public class GlobalController {
    @PostMapping("/trade")
    public ResponseEntity<?> signup(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new UserAuthException(ExceptionMessage.NOT_AUTHORIZED_ACCESS);
        }

        System.out.println(principal);
        return ResponseEntity.ok("hello world");
    }
}