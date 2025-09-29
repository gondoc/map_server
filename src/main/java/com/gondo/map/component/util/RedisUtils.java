package com.gondo.map.component.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    // key-value 저장
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // key-value 저장 + 만료시간 포함
    public void set(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    // key 로 value 조회
    public Optional<Object> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    // key 로 value 조회 + 타입 변환 포함
    public <T> Optional<T> get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (clazz.isInstance(value)) {
            return Optional.of(clazz.cast(value));
        }
        return Optional.empty();
    }

    // key-value 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // key 존재여부 확인
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // 만료 시간 설정
    public boolean expire(String key, long timeout) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, Duration.ofMinutes(timeout)));
    }

    // 만료 시간 조회
    public long getExpire(String key) {
        return Optional.ofNullable(redisTemplate.getExpire(key)).orElse(-1L);
    }

    // value 증가
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    // value 감소
    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }
}
