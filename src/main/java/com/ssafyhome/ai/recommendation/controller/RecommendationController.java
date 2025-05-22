package com.ssafyhome.ai.recommendation.controller;

import com.ssafyhome.ai.recommendation.dto.RecommendationDto;
import com.ssafyhome.ai.recommendation.service.RecommendationService;
import com.ssafyhome.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
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
    public Map<String, String> chat(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody RecommendationDto recommendationDto) {
        String result = recommendationService.recommendationArea(userDetails, recommendationDto);
        Map<String, String> response = new HashMap<>();
        response.put("recommend", result);

        return response;
    }

}
