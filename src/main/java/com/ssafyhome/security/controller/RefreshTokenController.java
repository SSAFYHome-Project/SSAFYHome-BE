package com.ssafyhome.security.controller;

import com.ssafyhome.security.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class RefreshTokenController {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserDetailsService userDetailsService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        String oldAccessToken = body.get("token");

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        String storedRefreshToken = redisTemplate.opsForValue().get("RT:" + email);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 정보가 일치하지 않거나 만료되었습니다.");
        }
        if (oldAccessToken != null && jwtTokenProvider.validateToken(oldAccessToken)) {
            redisTemplate.opsForValue().set(
                    oldAccessToken,
                    "logout",
                    Duration.ofMillis(jwtTokenProvider.getTokenValidityInMilliseconds())
            );
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(), null, userDetails.getAuthorities());

        String newAccessToken = jwtTokenProvider.generateToken(auth);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        redisTemplate.opsForValue().set(
                "RT:" + email,
                newRefreshToken,
                Duration.ofMillis(jwtTokenProvider.getRefreshTokenValidityInMilliseconds())
        );

        return ResponseEntity.ok(Map.of("token", newAccessToken, "refreshToken", newRefreshToken));
    }
}

