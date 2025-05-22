package com.ssafyhome.user.controller;

import com.ssafyhome.user.dto.UserInfo;
import com.ssafyhome.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserInfo>> checkEmailDuplicate(@RequestParam String keyword) {
        List<UserInfo> userInfos = userService.searchUserInfo(keyword);
        return ResponseEntity.ok(userInfos);


    }

}
