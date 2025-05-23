package com.ssafyhome.security.controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafyhome.security.config.JwtTokenProvider;
import com.ssafyhome.security.dto.LoginRequest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//        UsernamePasswordAuthenticationToken creds =
//                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            // JWT 생성
            String accessToken = jwtProvider.generateToken(authentication);
            String refreshToken = jwtProvider.generateRefreshToken(loginRequest.getEmail());

            redisTemplate.opsForValue().set(
                    loginRequest.getEmail(),
                    refreshToken,
                    jwtProvider.getRefreshTokenValidityInMilliseconds(),
                    java.util.concurrent.TimeUnit.MILLISECONDS
            );

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", jwtProvider.getTokenValidityInMilliseconds());

            // 비밀번호 변경 필요 여부 추가
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();
            response.put("passwordResetRequired", user.isPasswordResetRequired());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인에 실패했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest req) {
        String accessToken = jwtProvider.resolveToken(req);

        if (accessToken != null && jwtProvider.validateToken(accessToken)) {
            Authentication auth = jwtProvider.getAuthentication(accessToken);
            String email = auth.getName();

            redisTemplate.delete(email);

            Long expiration = jwtProvider.getExpiration(accessToken);
            if (expiration > 0) {
                redisTemplate.opsForValue().set(
                        accessToken,
                        "logout",
                        expiration,
                        java.util.concurrent.TimeUnit.MILLISECONDS
                );
            }

            return ResponseEntity.ok(Map.of(
                    "status", "로그아웃 성공",
                    "message", "AccessToken 블랙리스트 및 RefreshToken 제거 완료"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", "로그아웃 성공",
                "message", "이미 로그아웃된 상태입니다"
        ));
    }
}

