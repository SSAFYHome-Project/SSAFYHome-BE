package com.ssafyhome.community.recommend.service;

import com.ssafyhome.bookmark.dto.Bookmark;
import com.ssafyhome.community.board.dao.BoardRepository;
import com.ssafyhome.community.board.dto.Board;
import com.ssafyhome.community.recommend.dao.RecommendRepository;
import com.ssafyhome.community.recommend.dto.Recommend;
import com.ssafyhome.deal.dao.DealRepository;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private final RecommendRepository recommendRepository;
    private final BoardRepository boardRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public boolean toggleRecommend(CustomUserDetails userDetails, int boardIdx) {
        if (userDetails == null) throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        User user = userDetails.getUser();
        if (user == null) throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");

        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        if (board.getUser().getEmail().equals(user.getEmail())) {
            throw new IllegalArgumentException("자신의 게시글에는 추천할 수 없습니다.");
        }

        String recommendKey = "board:recommend:" + boardIdx;
        String userKey = "board:recommend:user:" + boardIdx + ":" + user.getEmail();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(userKey))) {
            // 이미 추천 → 취소
            redisTemplate.opsForValue().decrement(recommendKey);
            redisTemplate.delete(userKey);

            recommendRepository.findByUserAndBoard(user, board).ifPresent(recommendRepository::delete);
            return false; // 취소됨
        } else {
            // 아직 추천 안됨 → 등록
            redisTemplate.opsForValue().increment(recommendKey);
            redisTemplate.opsForValue().set(userKey, "1");

            Recommend recommend = new Recommend();
            recommend.setUser(user);
            recommend.setBoard(board);
            recommendRepository.save(recommend);
            return true; // 추천됨
        }
    }


}
