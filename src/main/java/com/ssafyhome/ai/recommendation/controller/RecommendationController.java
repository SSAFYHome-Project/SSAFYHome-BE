package com.ssafyhome.ai.recommendation.controller;

import com.ssafyhome.ai.recommendation.dto.RecommendationDto;
import com.ssafyhome.ai.recommendation.service.RecommendationService;
import com.ssafyhome.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/recommendation")
    public ResponseEntity<?> chat(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody RecommendationDto recommendationDto) {
        try {
            String result = recommendationService.recommendationArea(userDetails, recommendationDto);
            Map<String, String> response = new HashMap<>();
            response.put("recommend", result);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다: " + e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("추천 서비스 처리 중 오류가 발생했습니다.");
        }
    }

}
