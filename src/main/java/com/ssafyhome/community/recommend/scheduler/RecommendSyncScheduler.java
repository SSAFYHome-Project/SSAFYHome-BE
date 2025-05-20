package com.ssafyhome.community.recommend.scheduler;

import com.ssafyhome.community.board.dao.BoardRepository;
import com.ssafyhome.community.board.dto.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendSyncScheduler {

    private final BoardRepository boardRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void syncRecommendCountsToDB() {
        List<Board> boards = boardRepository.findAll();

        for (Board board : boards) {
            String key = "board:recommend:" + board.getBoardIdx();
            Object countObj = redisTemplate.opsForValue().get(key);

            if (countObj != null) {
                int redisCount = Integer.parseInt(countObj.toString());
                if (board.getBoardRecommendCnt() != redisCount) {
                    board.setBoardRecommendCnt(redisCount);
                    boardRepository.save(board);
                }
            }
        }
    }
}
