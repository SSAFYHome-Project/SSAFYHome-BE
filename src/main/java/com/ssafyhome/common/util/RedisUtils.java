package com.ssafyhome.common.util;

import com.ssafyhome.common.exception.BusinessException;
import com.ssafyhome.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtils {

    /**
     * Execute Redis operation with fallback
     */
    public static <T> T executeWithFallback(Supplier<T> redisOperation, T fallbackValue) {
        try {
            return redisOperation.get();
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure: {}", e.getMessage());
            return fallbackValue;
        } catch (Exception e) {
            log.error("Redis operation error: {}", e.getMessage());
            return fallbackValue;
        }
    }

    /**
     * Check if Redis is available
     */
    public static boolean isRedisAvailable(RedisTemplate<?, ?> redisTemplate) {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            return true;
        } catch (Exception e) {
            log.warn("Redis is not available: {}", e.getMessage());
            return false;
        }
    }

    public static <T> T getValue(RedisTemplate<String, T> redisTemplate, String key, Class<T> type) {
        try {
            if (!isRedisAvailable(redisTemplate)) {
                throw new BusinessException(ErrorCode.REDIS_CONNECTION_ERROR, "Redis 서버에 연결할 수 없습니다.");
            }
            return redisTemplate.opsForValue().get(key);
        } catch (RedisConnectionFailureException e) {
            throw new BusinessException(ErrorCode.REDIS_CONNECTION_ERROR, "Redis 연결 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new BusinessException("Redis에서 값을 조회하는 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    public static <T> void setValue(RedisTemplate<String, T> redisTemplate, String key, T value, long timeout, TimeUnit unit) {
        try {
            if (!isRedisAvailable(redisTemplate)) {
                throw new BusinessException(ErrorCode.REDIS_CONNECTION_ERROR, "Redis 서버에 연결할 수 없습니다.");
            }
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (RedisConnectionFailureException e) {
            throw new BusinessException(ErrorCode.REDIS_CONNECTION_ERROR, "Redis 연결 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new BusinessException("Redis에 값을 저장하는 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    public static <T> List<T> getList(RedisTemplate<String, T> redisTemplate, String key) {
        try {
            if (!isRedisAvailable(redisTemplate)) {
                throw new BusinessException(ErrorCode.REDIS_CONNECTION_ERROR, "Redis 서버에 연결할 수 없습니다.");
            }
            return redisTemplate.opsForList().range(key, 0, -1);
        } catch (RedisConnectionFailureException e) {
            throw new BusinessException(ErrorCode.REDIS_CONNECTION_ERROR, "Redis 연결 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new BusinessException("Redis에서 목록을 조회하는 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

}
