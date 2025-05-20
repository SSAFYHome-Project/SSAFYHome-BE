package com.ssafyhome.community.recommend.dao;

import com.ssafyhome.community.board.dto.Board;
import com.ssafyhome.community.recommend.dto.Recommend;
import com.ssafyhome.user.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, Integer> {
    Optional<Recommend> findByUserAndBoard(User user, Board board);
}