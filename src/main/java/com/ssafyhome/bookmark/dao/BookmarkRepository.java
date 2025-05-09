package com.ssafyhome.bookmark.dao;

import com.ssafyhome.bookmark.dto.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafyhome.user.dto.User;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Integer> {
    List<Bookmark> findByUser(User user);
}