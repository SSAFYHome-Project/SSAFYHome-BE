package com.ssafyhome.community.recommend.dao;

import com.ssafyhome.community.recommend.dto.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, Integer> {

}