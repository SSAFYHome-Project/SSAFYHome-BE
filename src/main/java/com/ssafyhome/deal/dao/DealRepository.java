package com.ssafyhome.deal.dao;

import com.ssafyhome.deal.dto.Deal;
import com.ssafyhome.deal.dto.dealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealRepository extends JpaRepository<Deal, Integer> {
    Optional<Deal> findByAptNameAndDealTypeAndRegionCodeAndJibunAndDealAmountAndDealYearAndDealMonthAndDealDayAndFloor(
            String aptName,
            dealType dealType,
            String regionCode,
            String jibun,
            int dealAmount,
            int dealYear,
            int dealMonth,
            int dealDay,
            int floor
    );
}