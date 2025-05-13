package com.ssafyhome.bookmark.controller;

import com.ssafyhome.bookmark.dto.Bookmark;
import com.ssafyhome.deal.dto.DealInfo;
import com.ssafyhome.bookmark.service.BookmarkService;
import com.ssafyhome.security.dto.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping("/bookmark")
    public ResponseEntity<?> getBookmarks(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<Bookmark> bookmarks = bookmarkService.getBookmark(userDetails);
            return ResponseEntity.ok(bookmarks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("북마크 조회 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/bookmark")
    public ResponseEntity<?> saveBookmark(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody DealInfo bookmarkInfo) {
        try {
            bookmarkService.saveBookmark(userDetails, bookmarkInfo);
            return ResponseEntity.ok("북마크가 저장되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("북마크 저장 중 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("/bookmark/{bookmarkIdx}")
    public ResponseEntity<?> deleteBookmark(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable int bookmarkIdx) {
        try {
            bookmarkService.deleteBookmark(userDetails, bookmarkIdx);
            return ResponseEntity.ok("삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예기치 못한 오류가 발생했습니다.");
        }
    }
}
