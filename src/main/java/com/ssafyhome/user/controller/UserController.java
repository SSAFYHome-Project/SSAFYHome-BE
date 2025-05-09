package com.ssafyhome.user.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafyhome.user.dto.RegisterRequest;
import com.ssafyhome.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	
    private final UserService userService;

    //form-data로 전송
    @PostMapping(
            value    = "/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> signup(@RequestPart("data") RegisterRequest request, @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        request.setProfileImage(profileImage);
        userService.signup(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @GetMapping("/register/dup")
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String email) {
        boolean isDuplicate = userService.isEmailDuplicate(email);
        // true면 중복된 이메일
        return ResponseEntity.ok(isDuplicate);
    }

}
