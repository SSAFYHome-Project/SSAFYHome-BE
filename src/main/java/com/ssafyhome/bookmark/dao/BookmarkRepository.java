package com.ssafyhome.bookmark.dao;

import com.ssafyhome.bookmark.dto.Bookmark;
import com.ssafyhome.deal.dto.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafyhome.user.dto.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Integer> {
    List<Bookmark> findByUser(User user);
    Optional<Bookmark> findByUserAndDeal(User user, Deal deal);

}