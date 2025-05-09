package com.ssafyhome.security.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/methodlevelSecure")
public class MethodLevelSecureController {

    // TODO: 12-1. ROLE_ADMIN 권한이 있을 경우만 접근을 허용하는 메서드를 확인하세요.
    @GetMapping("/user")
    @Secured("ROLE_USER")
    public String forUser() {
        return "secured/user/user";
    }

    @GetMapping("/admin")
    @Secured("ROLE_ADMIN")
    public String forAdmin() {
        return "secured/admin/admin";
    }

}
