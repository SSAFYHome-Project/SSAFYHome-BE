package com.ssafyhome.recentSearch.service;

import com.ssafyhome.deal.dto.DealInfo;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentViewService {
    public final RedisTemplate<String, DealInfo> recentViewRedisTemplate;

    public void saveRecentView(CustomUserDetails userDetails, DealInfo dealInfo) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        User user = userDetails.getUser();

        if (user == null) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        String key = user.getEmail();

        List<DealInfo> existingList = recentViewRedisTemplate.opsForList().range(key, 0, -1);
        if (existingList != null && existingList.contains(dealInfo)) {
            return;
        }

        recentViewRedisTemplate.opsForList().leftPush(key, dealInfo);

        Long size = recentViewRedisTemplate.opsForList().size(key);
        if (size != null && size > 5) {
            recentViewRedisTemplate.opsForList().rightPop(key);
        }


    }

    public List<DealInfo> getRecentView(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        User user = userDetails.getUser();

        if (user == null) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }
        String key = user.getEmail();
        return recentViewRedisTemplate.opsForList().range(key, 0, -1);
    }

    public void deleteRecentView(CustomUserDetails userDetails, DealInfo dealInfo) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        User user = userDetails.getUser();

        if (user == null) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }
        String key = user.getEmail();

        List<DealInfo> recentList = recentViewRedisTemplate.opsForList().range(key, 0, -1);

        if (recentList == null || !recentList.contains(dealInfo)) {
            throw new IllegalArgumentException("해당 매물이 최근 본 매물 목록에 존재하지 않습니다.");
        }

        recentViewRedisTemplate.opsForList().remove(key, 0, dealInfo);
    }


}
