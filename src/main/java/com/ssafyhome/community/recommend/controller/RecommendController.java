package com.ssafyhome.community.recommend.controller;

import com.ssafyhome.community.recommend.service.RecommendService;
import com.ssafyhome.security.dto.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommandService;

    @GetMapping("/board/{boardIdx}/recommend")
    public ResponseEntity<?> getBookmarks(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return null;
    }

}
