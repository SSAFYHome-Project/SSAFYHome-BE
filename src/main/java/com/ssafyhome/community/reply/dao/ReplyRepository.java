package com.ssafyhome.community.reply.dao;

import com.ssafyhome.bookmark.dto.Bookmark;
import com.ssafyhome.community.reply.dto.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer> {

}