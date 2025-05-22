package com.ssafyhome.community.reply.controller;

import com.ssafyhome.community.reply.dto.ReplyDetailDto;
import com.ssafyhome.community.reply.dto.ReplyRegisterRequest;
import com.ssafyhome.community.reply.service.ReplyService;
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
public class ReplyController {
    private final ReplyService replyService;

    @GetMapping("/board/{boardIdx}/reply")
    public ResponseEntity<?> getReply(@PathVariable int boardIdx) {
        List<ReplyDetailDto> replyList = replyService.getReply(boardIdx);
        return new ResponseEntity<>(replyList, HttpStatus.OK);
    }

    @PostMapping("/board/{boardIdx}/reply")
    public ResponseEntity<?> postReply(@AuthenticationPrincipal CustomUserDetails userDetails
    , @PathVariable int boardIdx, @RequestBody ReplyRegisterRequest replyRegisterRequest) {
        replyService.saveReply(userDetails, replyRegisterRequest, boardIdx);
        return ResponseEntity.ok("댓글 등록 완료");
    }

}
