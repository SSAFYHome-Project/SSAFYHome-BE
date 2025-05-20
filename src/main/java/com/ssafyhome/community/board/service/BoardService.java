package com.ssafyhome.community.board.service;

import com.ssafyhome.community.board.dao.BoardRepository;
import com.ssafyhome.community.board.dto.*;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public List<AllBoardDto> getAllBoards() {
        return boardRepository.findAllBoards();
    }

    public BoardDetailDto getBoardById(int boardIdx) {
        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 없습니다"));

        return new BoardDetailDto(
                board.getBoardIdx(),
                board.getBoardTitle(),
                board.getBoardContent(),
                board.getBoardView(),
                board.getUser().getName(),
                board.getBoardImage(),
                board.getBoardRegDate(),
                board.getBoardRecommendCnt()
        );
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
        if (StringUtils.hasText(request.getImage())) {
            board.setBoardImage(request.getImage());
        } else {
            board.setBoardImage(null); // 혹은 기본 이미지 URL 넣기
        }
        board.setBoardView(0); // 초기 조회수
        board.setBoardRecommendCnt(0); // 초기 추천수
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

        if (request.getContent() != null) {
            board.setBoardContent(request.getContent());
        }

        if (request.getImage() != null) {
            board.setBoardImage(request.getImage());
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