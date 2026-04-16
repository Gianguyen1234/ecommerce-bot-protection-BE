package com.holydev.platform.botprotection.infrastructure.persistent.redis;

import com.holydev.platform.botprotection.domain.port.RequestSequencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisRequestSequenceAdapter implements RequestSequencePort {

    // Redis template để thao tác với Redis
    private final StringRedisTemplate redisTemplate;

    @Override
    public void recordPath(String key, String path) {

        // thêm path mới vào đầu list (request mới nhất)
        // ví dụ: ["/home", "/product/:id", "/cart"]
        redisTemplate.opsForList().leftPush(key, path);

        // giữ tối đa 20 path gần nhất
        // tránh list dài vô hạn
        redisTemplate.opsForList().trim(key, 0, 20);

        // TTL = 5 phút (lâu hơn timestamp rule)
        // vì behavior path cần quan sát lâu hơn
        redisTemplate.expire(key, Duration.ofMinutes(5));
    }

    @Override
    public List<String> getRecentPaths(String key, int limit) {

        // lấy các path gần nhất từ index 0 → limit
        List<String> list = redisTemplate.opsForList().range(key, 0, limit);

        // nếu chưa có data → trả list rỗng
        return list != null ? list : List.of();
    }
}
