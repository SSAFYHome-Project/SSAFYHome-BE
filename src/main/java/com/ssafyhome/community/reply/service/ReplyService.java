package com.ssafyhome.community.reply.service;

import com.ssafyhome.common.util.UserUtils;
import com.ssafyhome.community.board.dao.BoardRepository;
import com.ssafyhome.community.board.dto.Board;
import com.ssafyhome.community.reply.dao.ReplyRepository;
import com.ssafyhome.community.reply.dto.Reply;
import com.ssafyhome.community.reply.dto.ReplyDetailDto;
import com.ssafyhome.community.reply.dto.ReplyRegisterRequest;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public List<ReplyDetailDto> getReply(int boardIdx) {
        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 없습니다"));
        List<ReplyDetailDto> replies = replyRepository.findByBoard(board);
        return replies;
    }

    @Transactional
    public void saveReply(CustomUserDetails userDetails, ReplyRegisterRequest replyRegisterRequest,
                          int boardIdx) {
        User user = UserUtils.getUserFromUserDetails(userDetails);

        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 없습니다"));

        Reply reply = new Reply();
        reply.setUser(user);
        reply.setReplyContent(replyRegisterRequest.getReplyContent());
        reply.setBoard(board);
        replyRepository.save(reply);
    }

}
