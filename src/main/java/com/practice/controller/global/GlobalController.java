package com.practice.controller.global;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/global")
public class GlobalController {
    @GetMapping("/trade")
    public String loginPage() {
        return "global/trade";
    }
}
