package com.ssafyhome.community.reply.controller;

import com.ssafyhome.bookmark.dto.Bookmark;
import com.ssafyhome.bookmark.service.BookmarkService;
import com.ssafyhome.community.reply.service.ReplyService;
import com.ssafyhome.deal.dto.DealInfo;
import com.ssafyhome.security.dto.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    @GetMapping("/board/{boardIdx}/reply")
    public ResponseEntity<?> getBookmarks(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            return ResponseEntity.ok(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("북마크 조회 중 오류가 발생했습니다.");
        }
    }

}
