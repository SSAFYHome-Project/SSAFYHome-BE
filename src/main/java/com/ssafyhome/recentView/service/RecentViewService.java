package com.ssafyhome.recentView.service;

import com.ssafyhome.common.util.UserUtils;
import com.ssafyhome.deal.dto.DealInfo;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecentViewService {

    private final RedisTemplate<String, DealInfo> recentViewRedisTemplate;

    private String getRedisKey(User user) {
        return "user:" + user.getEmail() + ":recentView";
    }


    public List<DealInfo> getRecentView(CustomUserDetails userDetails) {
        try {

            User user = UserUtils.getUserFromUserDetails(userDetails);
            String key = getRedisKey(user);

            if (!isRedisAvailable()) return new ArrayList<>();

            List<DealInfo> result = recentViewRedisTemplate.opsForList().range(key, 0, -1);
            return result != null ? result : new ArrayList<>();

        } catch (RedisSystemException e) {
            log.error("Redis 시스템 오류로 최근 본 매물을 불러올 수 없습니다: {}", e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("최근 본 매물 조회 중 오류 발생: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveRecentView(CustomUserDetails userDetails, DealInfo dealInfo) {
        try {
            User user = UserUtils.getUserFromUserDetails(userDetails);
            String key = "user:" + user.getEmail() + ":recentView";

            List<DealInfo> existingList = recentViewRedisTemplate.opsForList().range(key, 0, -1);
            if (existingList != null) {
                // 완전히 같은 DealInfo 객체가 있으면 제거
                for (DealInfo existing : existingList) {
                    if (isDealInfoEqual(existing, dealInfo)) {
                        recentViewRedisTemplate.opsForList().remove(key, 0, existing);
                        break;
                    }
                }
            }

            recentViewRedisTemplate.opsForList().leftPush(key, dealInfo);

            Long size = recentViewRedisTemplate.opsForList().size(key);
            if (size != null && size > 5) {
                recentViewRedisTemplate.opsForList().rightPop(key);
            }

        } catch (Exception e) {
            log.error("최근 본 매물 저장 중 오류 발생: {}", e.getMessage());
        }
    }

    private boolean isDealInfoEqual(DealInfo deal1, DealInfo deal2) {
        if (deal1 == null || deal2 == null) return false;

        return Objects.equals(deal1.getAptName(), deal2.getAptName()) &&
                deal1.getDealType() == deal2.getDealType() &&
                Objects.equals(deal1.getRegionCode(), deal2.getRegionCode()) &&
                Objects.equals(deal1.getJibun(), deal2.getJibun()) &&
                deal1.getDeposit() == deal2.getDeposit() &&
                deal1.getMonthlyRent() == deal2.getMonthlyRent() &&
                deal1.getDealAmount() == deal2.getDealAmount() &&
                Float.compare(deal1.getExcluUseAr(), deal2.getExcluUseAr()) == 0 &&
                deal1.getDealYear() == deal2.getDealYear() &&
                deal1.getDealMonth() == deal2.getDealMonth() &&
                deal1.getDealDay() == deal2.getDealDay() &&
                deal1.getFloor() == deal2.getFloor() &&
                deal1.getBuildYear() == deal2.getBuildYear();
    }

    public void deleteRecentView(CustomUserDetails userDetails, DealInfo dealInfo) {
        try {
            User user = UserUtils.getUserFromUserDetails(userDetails);
            String key = getRedisKey(user);

            List<DealInfo> recentList = recentViewRedisTemplate.opsForList().range(key, 0, -1);
            if (recentList == null || !recentList.contains(dealInfo)) {
                throw new IllegalArgumentException("해당 매물이 최근 본 매물 목록에 존재하지 않습니다.");
            }

            recentViewRedisTemplate.opsForList().remove(key, 0, dealInfo);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("최근 본 매물 삭제 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("최근 본 매물 삭제 중 오류가 발생했습니다.", e);
        }
    }

    private boolean isRedisAvailable() {
        try {
            recentViewRedisTemplate.getConnectionFactory().getConnection().ping();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}