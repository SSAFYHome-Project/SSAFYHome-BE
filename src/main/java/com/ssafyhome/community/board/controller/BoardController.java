package com.ssafyhome.community.board.controller;

import com.ssafyhome.community.board.dto.*;
import com.ssafyhome.community.board.service.BoardService;
import com.ssafyhome.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/board")
    public ResponseEntity<?> getAllBoard() {
        try {
            List<AllBoardDto> boards = boardService.getAllBoards();
            return ResponseEntity.ok(boards);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 목록 조회 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("board/{boardIdx}")
    public ResponseEntity<?> getBoard(@PathVariable int boardIdx) {
        try {
            BoardDetailDto board = boardService.getBoardById(boardIdx);
            return ResponseEntity.ok(board);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 게시글입니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 조회 중 오류가 발생했습니다.");
        }
    }

    @PostMapping(value = "/board", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postBoard(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @ModelAttribute BoardRegisterRequest boardRegisterRequest) {
        try {
            boardService.saveBoard(boardRegisterRequest, userDetails);
            return ResponseEntity.ok("게시글 등록 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다: " + e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 등록 중 오류가 발생했습니다.");
        }
    }

    @PatchMapping(value = "board/{boardIdx}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateBoard(@PathVariable int boardIdx,
                                         @AuthenticationPrincipal CustomUserDetails userDetails,
                                         @ModelAttribute BoardPatchRequest boardPatchRequest) {
        try {
            boardService.updateBoard(boardIdx, boardPatchRequest, userDetails);
            return ResponseEntity.ok("게시글 수정 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다: " + e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 수정 중 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("board/{boardIdx}")
    public ResponseEntity<?> deleteBoard(@PathVariable int boardIdx, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            boardService.deleteBoard(boardIdx, userDetails);
            return ResponseEntity.ok("게시글 삭제 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 게시글입니다: " + e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 삭제 중 오류가 발생했습니다.");
        }
    }


}
