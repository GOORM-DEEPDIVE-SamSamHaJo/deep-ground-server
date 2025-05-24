package com.samsamhajo.deepground.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "RT:";

    public void save(Long memberId, String refreshToken, Long expirationTime) {
        String key = generateKey(memberId);
        redisTemplate.opsForValue().set(key, refreshToken, expirationTime, TimeUnit.SECONDS);
    }

    public String findByMemberId(Long memberId) {
        String key = generateKey(memberId);
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteByMemberId(Long memberId) {
        String key = generateKey(memberId);
        redisTemplate.delete(key);
    }

    public boolean hasRefreshToken(Long memberId) {
        String key = generateKey(memberId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private String generateKey(Long memberId) {
        return KEY_PREFIX + memberId;
    }
}
