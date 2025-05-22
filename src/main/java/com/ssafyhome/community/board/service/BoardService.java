package com.ssafyhome.community.board.service;

import com.ssafyhome.community.board.dao.BoardRepository;
import com.ssafyhome.community.board.dto.*;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    public List<AllBoardDto> getAllBoards() {
        return boardRepository.findAllBoards();
    }

    public BoardDetailDto getBoardById(int boardIdx) {
        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 없습니다"));

        String recommendKey = "board:view:" + boardIdx;
        redisTemplate.opsForValue().increment(recommendKey);

        int recommendCount = getRecommendCount(boardIdx);
        int viewCount = getViewCount(boardIdx);

        return new BoardDetailDto(
                board.getBoardIdx(),
                board.getBoardTitle(),
                board.getBoardContent(),
                viewCount,
                board.getUser().getName(),
                board.getBoardRegDate(),
                recommendCount,
                board.getUser().getEmail(),
                board.getBoardCategory()
        );
    }

    public int getRecommendCount(int boardIdx) {
        String key = "board:recommend:" + boardIdx;
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? Integer.parseInt(count.toString()) : 0;
    }

    public int getViewCount(int boardIdx) {
        String key = "board:view:" + boardIdx;
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? Integer.parseInt(count.toString()) : 0;
    }

    public void saveBoard(BoardRegisterRequest request, CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        User user = userDetails.getUser();

        if (user == null) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }
        Board board = new Board();
        board.setBoardTitle(request.getTitle());
        board.setBoardContent(request.getContent());
        board.setBoardView(0);
        board.setBoardRecommendCnt(0);
        board.setBoardCategory(request.getCategory());
        board.setUser(user);

        boardRepository.save(board);
    }



    public void updateBoard(int boardIdx, BoardPatchRequest request, CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        User user = userDetails.getUser();

        if (user == null) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        boolean isAuthor = boardRepository.existsByBoardIdxAndUser(boardIdx, user);

        if (!isAuthor) {
            throw new IllegalArgumentException("게시글 작성자만 수정할 수 있습니다.");
        }

        if (request.getTitle() != null) {
            board.setBoardTitle(request.getTitle());
        }

        if (request.getCategory() != null) {
            board.setBoardCategory(request.getCategory());
        }


        if (request.getContent() != null) {
            board.setBoardContent(request.getContent());
        }

        boardRepository.save(board);

    }

    public void deleteBoard(int boardIdx, CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        User user = userDetails.getUser();

        if (user == null) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        boardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        boolean isAuthor = boardRepository.existsByBoardIdxAndUser(boardIdx, user);

        if (!isAuthor) {
            throw new IllegalArgumentException("게시글 작성자만 삭제할 수 있습니다.");
        }

        boardRepository.deleteById(boardIdx);

    }

}