package com.ssafyhome.user.controller;

import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.PasswordChangeRequest;
import com.ssafyhome.user.dto.PasswordResetRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ssafyhome.user.dto.UserRegisterRequest;
import com.ssafyhome.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	
    private final UserService userService;

    //form-data로 전송
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> signup(
        @RequestPart("data") UserRegisterRequest request,
        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        try {
            request.setProfileImage(profileImage);
            userService.signup(request);
            return ResponseEntity.ok("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 처리 중 오류가 발생했습니다.");
        }
    }


    @GetMapping("/register/dup")
    public ResponseEntity<?> checkEmailDuplicate(@RequestParam String email) {
        try {
            boolean isDuplicate = userService.isEmailDuplicate(email);
            // true면 중복된 이메일
            return ResponseEntity.ok(isDuplicate);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 이메일 형식입니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이메일 중복 확인 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            userService.resetPassword(request.getEmail());
            return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 재설정 중 오류가 발생했습니다.");
        }

    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            userService.changePassword(userDetails.getUsername(), request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("비밀번호 변경 중 오류가 발생했습니다.");
        }
    }
}
