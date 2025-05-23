package com.ssafyhome.community.board.service;

import com.ssafyhome.common.exception.BusinessException;
import com.ssafyhome.common.exception.ErrorCode;
import com.ssafyhome.common.util.UserUtils;
import com.ssafyhome.community.board.dao.BoardImageRepository;
import com.ssafyhome.community.board.dao.BoardRepository;
import com.ssafyhome.community.board.dto.*;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final S3Service s3Service;

    @Transactional
    public List<AllBoardDto> getAllBoards() {
        return boardRepository.findAllBoards();
    }

    @Transactional
    public BoardDetailDto getBoardById(int boardIdx) {
        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 없습니다"));

        incrementViewCount(boardIdx);

        int recommendCount = getRecommendCount(boardIdx);
        int viewCount = getViewCount(boardIdx);

        List<String> imageUrls = board.getImages().stream()
                .map(BoardImage::getImageUrl)
                .collect(Collectors.toList());

        BoardDetailDto boardDetailDto = new BoardDetailDto(
                board.getBoardIdx(),
                board.getBoardTitle(),
                board.getBoardContent(),
                viewCount,
                board.getUser().getName(),
                board.getBoardRegDate(),
                recommendCount,
                board.getUser().getEmail(),
                board.getBoardCategory(),
                board.getUser().getProfile()
        );

        boardDetailDto.setImageUrls(imageUrls);
        return boardDetailDto;
    }
    private void incrementViewCount(int boardIdx) {
        try {
            String recommendKey = "board:view:" + boardIdx;
            redisTemplate.opsForValue().increment(recommendKey);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
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

    @Transactional
    public void saveBoard(BoardRegisterRequest request, CustomUserDetails userDetails) {
        User user = UserUtils.getUserFromUserDetails(userDetails);

        Board board = new Board();
        board.setBoardTitle(request.getTitle());
        board.setBoardContent(request.getContent());
        board.setBoardView(0);
        board.setBoardRecommendCnt(0);
        board.setBoardCategory(request.getCategory());
        board.setUser(user);

        boardRepository.save(board);

        // 이미지 처리
        processImages(board, request.getImages());
    }


    @Transactional
    public void updateBoard(int boardIdx, BoardPatchRequest request, CustomUserDetails userDetails) {
        User user = UserUtils.getUserFromUserDetails(userDetails);

        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        validateBoardOwnership(board, user);

        if (request.getTitle() != null) {
            board.setBoardTitle(request.getTitle());
        }

        if (request.getCategory() != null) {
            board.setBoardCategory(request.getCategory());
        }


        if (request.getContent() != null) {
            board.setBoardContent(request.getContent());
        }
        // 이미지 삭제 처리
        deleteRequestedImages(board, request.getImagesToDelete());

        // 새 이미지 추가
        processImages(board, request.getNewImages());

        boardRepository.save(board);

    }

    private void deleteRequestedImages(Board board, List<String> imagesToDelete) {
        if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
            for (String imageUrl : imagesToDelete) {
                String s3Key = s3Service.getS3KeyFromUrl(imageUrl);

                // 이미지 엔티티 찾기 및 삭제
                board.getImages().removeIf(image -> image.getS3Key().equals(s3Key));

                // S3에서 파일 삭제
                s3Service.deleteFile(s3Key);
            }
        }
    }

    @Transactional
    public void deleteBoard(int boardIdx, CustomUserDetails userDetails) {
        User user = UserUtils.getUserFromUserDetails(userDetails);

        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        validateBoardOwnership(board, user);

        // S3에서 이미지 삭제
        for (BoardImage image : board.getImages()) {
            s3Service.deleteFile(image.getS3Key());
        }


        boardRepository.deleteById(boardIdx);

    }

    private void validateBoardOwnership(Board board, User user) {
        boolean isAuthor = board.getUser().getMno() == user.getMno();
        if (!isAuthor) {
            throw new BusinessException(ErrorCode.NOT_BOARD_OWNER, "게시글 작성자만 수정/삭제할 수 있습니다.");
        }
    }

    private void processImages(Board board, List<MultipartFile> images) {
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                try {
                    String imageUrl = s3Service.uploadFile(image);
                    String s3Key = s3Service.getS3KeyFromUrl(imageUrl);
                    BoardImage boardImage = new BoardImage(imageUrl, s3Key);
                    board.addImage(boardImage);
                } catch (IOException e) {
                    throw new BusinessException("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
                }
            }
            boardRepository.save(board);
        }
    }

}