package com.ssafyhome.security.config;

import com.ssafyhome.security.oauth.OAuth2CustomUserService;
import com.ssafyhome.security.oauth.OAuthFailureHandler;
import com.ssafyhome.security.oauth.OAuthSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig {


    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final OAuth2CustomUserService oAuth2CustomUserService;

    @Bean
    public OAuthSuccessHandler oAuthSuccessHandler(){
        return new OAuthSuccessHandler(jwtTokenProvider);
    }

    @Bean
    public OAuthFailureHandler oAuthFailureHandler() {
        return new OAuthFailureHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .sessionManagement(sm -> sm
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/map/**", "/api/login", "/api/user/register/**", "/api/apt/**").permitAll()
                .requestMatchers("/api/user/**", "/api/logout").hasAnyRole("USER", "ADMIN")
//                .requestMatchers("/api/logout").authenticated()
            .anyRequest().authenticated()
        )
                .oauth2Login(oauth2 -> oauth2
                                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                        .userService(oAuth2CustomUserService))
                        .successHandler(oAuthSuccessHandler())
                        .failureHandler(oAuthFailureHandler()) // 로그인 실패 시 경로
                )
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate),
            UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // AuthenticationManager를 빈으로 등록(로그인 로직에서 사용)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
