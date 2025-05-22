package com.ssafyhome.community.board.controller;

import com.ssafyhome.community.board.dto.*;
import com.ssafyhome.community.board.service.BoardService;
import com.ssafyhome.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        List<AllBoardDto> boards = boardService.getAllBoards();
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @GetMapping("board/{boardIdx}")
    public ResponseEntity<?> getBoard(@PathVariable int boardIdx) {
        BoardDetailDto board = boardService.getBoardById(boardIdx);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @PostMapping("/board")
    public ResponseEntity<?> postBoard(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @RequestBody BoardRegisterRequest boardRegisterRequest) {
        boardService.saveBoard(boardRegisterRequest, userDetails);
        return ResponseEntity.ok("게시글 등록 완료");
    }

    @PatchMapping("board/{boardIdx}")
    public ResponseEntity<?> updateBoard(@PathVariable int boardIdx, @AuthenticationPrincipal CustomUserDetails userDetails,
                                      @RequestBody BoardPatchRequest boardPatchRequest) {
        boardService.updateBoard(boardIdx, boardPatchRequest, userDetails);
        return ResponseEntity.ok("게시글 수정 완료");
    }

    @DeleteMapping("board/{boardIdx}")
    public ResponseEntity<?> deleteBoard(@PathVariable int boardIdx, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boardService.deleteBoard(boardIdx, userDetails);
        return ResponseEntity.ok("게시글 삭제 완료");
    }


}
