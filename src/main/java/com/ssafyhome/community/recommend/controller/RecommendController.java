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

    @PostMapping("/board/{boardIdx}/recommend")
    public ResponseEntity<?> postRecommend(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable int boardIdx) {
        recommandService.postRecommend(userDetails, boardIdx);
        return ResponseEntity.ok("게시글 추천 완료");
    }

    @DeleteMapping("/board/{boardIdx}/recommend")
    public ResponseEntity<?> deleteRecommend(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable int boardIdx) {
        recommandService.deleteRecommend(userDetails, boardIdx);
        return ResponseEntity.ok("게시글 추천 취소 완료");
    }

}
