package com.jinishop.jinishop.page;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 메인 페이지
    @GetMapping("/")
    public String mainPage() {
        return "main";
    }

    // 관리자 페이지
    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }
}
