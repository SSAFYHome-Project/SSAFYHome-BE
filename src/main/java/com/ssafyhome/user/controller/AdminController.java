package com.ssafyhome.user.controller;

import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.UserInfo;
import com.ssafyhome.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserInfo>> getAllUsers() {
        List<UserInfo> allUsers = userService.getAllUserInfo();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping
    public ResponseEntity<List<UserInfo>> searchUsers(@RequestParam String keyword) {
        List<UserInfo> userInfos = userService.searchUserInfo(keyword);
        return ResponseEntity.ok(userInfos);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserByAdmin(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String email) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }

        if (!"ROLE_ADMIN".equals(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }

        if (email.equals(userDetails.getEmail())) {
            return ResponseEntity.badRequest().body("관리자는 자신의 계정을 이 방식으로 삭제할 수 없습니다.");
        }

        try {
            userService.deleteUserByEmail(email);
            return ResponseEntity.ok("관리자가 " + email + " 회원을 삭제했습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원 삭제 중 오류가 발생했습니다.");
        }
    }
}
