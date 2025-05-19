package com.ssafyhome.security.oauth;

import com.ssafyhome.security.config.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.info("[OAuthSuccessHandler] OAuth 로그인 성공: {}", authentication.getName());

        // ✅ JWT 발급
        String accessToken = jwtTokenProvider.generateToken(authentication);


        // ✅ 리다이렉트할 URI 구성 (쿼리 파라미터에 JWT 포함)
        String targetUrl = UriComponentsBuilder.fromUriString(setRedirectUrl(request.getServerName()))
                .queryParam("jwtAccessToken", accessToken)
                .build().toUriString();

        log.info("[OAuthSuccessHandler] 리다이렉트 URL: {}", targetUrl);

        // ✅ 리다이렉트 수행
        redirectStrategy.sendRedirect(request, response, targetUrl);

    }

    private String setRedirectUrl(String url) {
        String redirect_url = null;

        /* 백엔드 개발 환경 */
        if (url.equals("localhost")) {
            redirect_url = "http://localhost:8080/oauth/google/success";
        }
        /* 프론트 개발 또는 프로덕션 환경 */
//        else {
//            log.info("backServerName: " + backServerName);
//            log.info("url: " + url);
//            log.info("frontRedirectUrl: " + frontRedirectUrl);
//            redirect_url = frontRedirectUrl + "/oauth/google/success/ing";
//        }
        return redirect_url;
    }
}
