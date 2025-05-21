package com.ssafyhome.security.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        UsernamePasswordAuthenticationToken creds =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        try {
            Authentication auth = authenticationManager.authenticate(creds);
            // JWT 생성
            String accessToken = jwtProvider.generateToken(auth);
            String refreshToken = jwtProvider.generateRefreshToken(loginRequest.getEmail());

            redisTemplate.opsForValue().set(
                    "RT:" + loginRequest.getEmail(),
                    refreshToken,
                    Duration.ofMillis(jwtProvider.getRefreshTokenValidityInMilliseconds()));

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(Map.of("status", "로그인 성공",
                            "token", accessToken,
                            "refreshToken", refreshToken));
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "로그인 실패", "message", "이메일 또는 비밀번호 오류"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest req) {
        String accessToken = jwtProvider.resolveToken(req);

        if (accessToken != null && jwtProvider.validateToken(accessToken)) {
            String email = jwtProvider.getEmailFromToken(accessToken);
            redisTemplate.opsForValue().set(
                    accessToken,
                    "logout",
                    Duration.ofMillis(jwtProvider.getTokenValidityInMilliseconds())
            );
            redisTemplate.delete("RT:" + email);
        }

        return ResponseEntity.ok(Map.of(
                "status", "로그아웃 성공",
                "message", "AccessToken 블랙리스트 및 RefreshToken 제거 완료"
        ));
    }
}

