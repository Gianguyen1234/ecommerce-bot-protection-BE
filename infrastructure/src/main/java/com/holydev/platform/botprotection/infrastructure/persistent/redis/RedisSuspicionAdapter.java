package com.holydev.platform.botprotection.infrastructure.persistent.redis;

import com.holydev.platform.botprotection.domain.port.SuspicionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisSuspicionAdapter implements SuspicionPort {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void markSuspicious(String key) {
        redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(60));
    }

    @Override
    public boolean isSuspicious(String key) {
        return redisTemplate.hasKey(key);
    }
}