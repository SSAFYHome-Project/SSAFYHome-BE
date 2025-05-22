package com.ssafyhome.map.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SafetyRepository extends JpaRepository<Safety, Integer> {
    Optional<Safety> findBySidoAndSigungu(String sido, String sigungu);
}