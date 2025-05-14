package com.ssafyhome.recentView.controller;

import com.ssafyhome.deal.dto.DealInfo;
import com.ssafyhome.recentView.service.RecentViewService;
import com.ssafyhome.security.dto.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class RecentViewController {
    private final RecentViewService recentViewService;

    @GetMapping("/recentView")
    public ResponseEntity<?> getRecentSearch(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {

            List<DealInfo> recentViewInfo =  recentViewService.getRecentView(userDetails);
            return ResponseEntity.ok(recentViewInfo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("최근 검색어 조회 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/recentView")
    public ResponseEntity<?> saveRecentViewed(@RequestBody DealInfo dealInfo,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            recentViewService.saveRecentView(userDetails, dealInfo);
            return ResponseEntity.ok("최근 본 매물에 추가되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("최근 본 매물 저장 중 오류가 발생했습니다.");
        }
    }


    @DeleteMapping("/recentView")
    public ResponseEntity<?> deleteRecentViewed(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestBody DealInfo dealInfo) {
        try {
            recentViewService.deleteRecentView(userDetails, dealInfo);
            return ResponseEntity.ok("최근 본 매물에서 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("최근 본 매물 삭제 중 오류가 발생했습니다.");
        }
    }


}
