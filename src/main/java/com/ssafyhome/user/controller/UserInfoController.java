package com.ssafyhome.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.UserPatchRequest;
import com.ssafyhome.user.dto.UserInfo;
import com.ssafyhome.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserInfoController {
	
    private final UserService userService;


    @GetMapping("/info")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }
        UserInfo userInfo = userService.getUserInfoFromDetails(userDetails);

        return ResponseEntity.ok(userInfo);

    }

    @PatchMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> patchUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestPart("data") UserPatchRequest userPatchRequest,
                                           @RequestPart(value = "profileImage", required = false) MultipartFile profileFile) {

        try {
            userService.updateUserInfo(userDetails, userPatchRequest);

            return ResponseEntity.ok("회원 정보가 성공적으로 업데이트되었습니다.");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 처리 중 오류가 발생했습니다.");
        }

    }

    @DeleteMapping("/info")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }

        try {
            userService.deleteUser(userDetails);

            return ResponseEntity.ok("회원 정보를 삭제했습니다.");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 처리 중 오류가 발생했습니다.");
        }


    }
}
