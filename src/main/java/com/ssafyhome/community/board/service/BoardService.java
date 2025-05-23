package com.ssafyhome.community.board.service;

import com.ssafyhome.community.board.dao.BoardImageRepository;
import com.ssafyhome.community.board.dao.BoardRepository;
import com.ssafyhome.community.board.dto.*;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.EntityNotFoundException;
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

        // 이미지 처리
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (MultipartFile image : request.getImages()) {
                try {
                    String imageUrl = s3Service.uploadFile(image);
                    String s3Key = s3Service.getS3KeyFromUrl(imageUrl);
                    BoardImage boardImage = new BoardImage(imageUrl, s3Key);
                    board.addImage(boardImage);
                } catch (IOException e) {
                    throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
                }
            }
            boardRepository.save(board);
        }
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
        // 이미지 삭제 처리
        if (request.getImagesToDelete() != null && !request.getImagesToDelete().isEmpty()) {
            for (String imageUrl : request.getImagesToDelete()) {
                String s3Key = s3Service.getS3KeyFromUrl(imageUrl);

                // 이미지 엔티티 찾기 및 삭제
                board.getImages().removeIf(image -> image.getS3Key().equals(s3Key));

                // S3에서 파일 삭제
                s3Service.deleteFile(s3Key);
            }
        }

        // 새 이미지 추가
        if (request.getNewImages() != null && !request.getNewImages().isEmpty()) {
            for (MultipartFile image : request.getNewImages()) {
                try {
                    String imageUrl = s3Service.uploadFile(image);
                    String s3Key = s3Service.getS3KeyFromUrl(imageUrl);
                    BoardImage boardImage = new BoardImage(imageUrl, s3Key);
                    board.addImage(boardImage);
                } catch (IOException e) {
                    throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
                }
            }
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

        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        boolean isAuthor = boardRepository.existsByBoardIdxAndUser(boardIdx, user);

        if (!isAuthor) {
            throw new IllegalArgumentException("게시글 작성자만 삭제할 수 있습니다.");
        }
        // S3에서 이미지 삭제
        for (BoardImage image : board.getImages()) {
            s3Service.deleteFile(image.getS3Key());
        }


        boardRepository.deleteById(boardIdx);

    }

}