package com.ssafyhome.community.reply.dao;

import com.ssafyhome.bookmark.dto.Bookmark;
import com.ssafyhome.community.board.dto.AllBoardDto;
import com.ssafyhome.community.board.dto.Board;
import com.ssafyhome.community.reply.dto.Reply;
import com.ssafyhome.community.reply.dto.ReplyDetailDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer> {

    @Query("SELECT new com.ssafyhome.community.reply.dto.ReplyDetailDto(" +
            "r.replyIdx, r.replyContent, r.replyRegDate, r.user.name, r.user.email) " +
            "FROM Reply r WHERE r.board = :board")
    List<ReplyDetailDto> findByBoard(@Param("board") Board board);
}