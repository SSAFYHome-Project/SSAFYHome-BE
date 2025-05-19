package com.ssafyhome.security.oauth;

import com.ssafyhome.user.dao.UserRepository;
import com.ssafyhome.user.dto.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class OAuth2CustomUserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 로그로 Google 응답 값 확인
        attributes.forEach((key, value) -> log.info("[google attribute] {} : {}", key, value));

        // Google 로그인 전용 처리
        String id = (String) attributes.get("id");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        // 이메일 기준으로 신규 회원이면 자동 가입
        if (!userRepository.existsByEmail(email)) {
            try {
                // 프로필 이미지 byte[]로 다운로드
                byte[] profileBytes = null;
                if (picture != null && !picture.isEmpty()) {
                    profileBytes = new URL(picture).openStream().readAllBytes();
                }

                User user = new User();
                user.setEmail(email);
                user.setPassword("social_login_user");
                user.setName(name);
                user.setRole("ROLE_USER");
                user.setProfile(profileBytes);

                userRepository.save(user);

            } catch (IOException e) {
                throw new RuntimeException("Google 프로필 이미지 처리 중 오류", e);
            }
        }

        User user = userRepository.findByEmail(email);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                attributes,
                "email" // 사용자 식별자 키
        );

    }

}