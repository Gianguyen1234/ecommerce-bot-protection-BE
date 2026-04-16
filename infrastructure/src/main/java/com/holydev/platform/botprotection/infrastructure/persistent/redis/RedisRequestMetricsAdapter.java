package com.holydev.platform.botprotection.infrastructure.persistent.redis;

import com.holydev.platform.botprotection.domain.port.RequestMetricsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class RedisRequestMetricsAdapter implements RequestMetricsPort {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> script;

    public RedisRequestMetricsAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        this.script = new DefaultRedisScript<>();
        this.script.setLocation(new ClassPathResource("redis/rate_limit.lua"));
        this.script.setResultType(Long.class);
    }

    @Override
    public long countRequests(String key, long windowSeconds) {

        long now = System.currentTimeMillis();
        long windowMillis = windowSeconds * 1000;

        Long result = redisTemplate.execute(
                script,
                Collections.singletonList(key),
                String.valueOf(now),
                String.valueOf(windowMillis)
        );

        return result != null ? result : 0;
    }
}