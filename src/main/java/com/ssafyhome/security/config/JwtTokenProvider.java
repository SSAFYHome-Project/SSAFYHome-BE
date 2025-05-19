package com.ssafyhome.security.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.crypto.SecretKey;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecretString;

    @Value("${jwt.token-validity-in-milliseconds}")
    private long tokenValidityInMilliseconds;

    private SecretKey secretKey;

    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecretString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public String generateToken(Authentication auth) {
        String username = auth.getName();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + tokenValidityInMilliseconds);

        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(username)
                .claim("auth", roles)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // HTTP 요청 헤더에서 토큰 추출
    public String resolveToken(HttpServletRequest req) {
        String bearer = req.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer "))
                ? bearer.substring(7)
                : null;
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            // log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            // log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            // log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // 토큰에서 Authentication 반환
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();

        // 권한 복원
        List<SimpleGrantedAuthority> authorities = Optional.ofNullable((List<?>) claims.get("auth"))
                .orElse(List.of()).stream()
                .map(role -> new SimpleGrantedAuthority((String) role))
                .collect(Collectors.toList());

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
}

