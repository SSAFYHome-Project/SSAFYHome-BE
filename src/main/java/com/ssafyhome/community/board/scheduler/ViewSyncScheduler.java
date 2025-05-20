package com.ssafyhome.community.board.scheduler;


import com.ssafyhome.community.board.dao.BoardRepository;
import com.ssafyhome.community.board.dto.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ViewSyncScheduler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final BoardRepository boardRepository;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void syncViewCounts() {
        List<Board> boards = boardRepository.findAll();
        for (Board board : boards) {
            String key = "board:view:" + board.getBoardIdx();
            Object redisCount = redisTemplate.opsForValue().get(key);
            if (redisCount != null) {
                int count = Integer.parseInt(redisCount.toString());
                board.setBoardView(count);
                boardRepository.save(board);
            }
        }
    }
}

