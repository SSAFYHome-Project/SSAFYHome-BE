package com.ssafyhome.bookmark.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafyhome.user.dto.User;

@Repository
public interface BookmarkRepository extends JpaRepository<User, Integer> {

}