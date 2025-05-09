package com.ssafyhome.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {
    	HttpServletRequest httpRequest = (HttpServletRequest) req;

        String uri = httpRequest.getRequestURI();
    	
    	if (uri.startsWith("/api/map") || uri.startsWith("/api/login") || uri.startsWith("/api/user/register")) {
    		log.info("[JWT Filter] Bypassed authentication for URL: {}", uri);
            chain.doFilter(req, res); // 필터 통과
            return;
        }

        // 1) 토큰 추출
        String token = jwtProvider.resolveToken(req);

        // 2) 요청 URL과 토큰을 로그로 찍어보기
        log.info("[JWT Filter] request URL: {}", req.getRequestURL());
        log.info("[JWT Filter] token: {}", token);

        // 3) 토큰 유효성 검사
        if (token != null && jwtProvider.validateToken(token)) {
            // 4) Redis에서 해당 토큰의 로그아웃 여부 확인
            String isLogout = redisTemplate.opsForValue().get(token);
            if (isLogout == null) {
                // 5) 토큰이 유효하고, 로그아웃 처리된 적이 없으면 Authentication 생성
                Authentication auth = jwtProvider.getAuthentication(token);
                // 6) SecurityContext에 저장
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("[JWT Filter] Authentication set for user: {}", auth.getName());
            }
        }

        // 7) 필터 체인 계속
        chain.doFilter(req, res);
    }
}
