package com.ssafyhome.community.board.dao;

import com.ssafyhome.community.board.dto.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardImageRepository extends JpaRepository<BoardImage, Integer> {
    List<BoardImage> findByBoardBoardIdx(int boardIdx);
    void deleteByS3Key(String s3Key);
}

