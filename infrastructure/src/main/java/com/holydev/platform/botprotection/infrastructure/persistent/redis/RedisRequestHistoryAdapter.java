package com.holydev.platform.botprotection.infrastructure.persistent.redis;

import com.holydev.platform.botprotection.domain.port.RequestHistoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisRequestHistoryAdapter implements RequestHistoryPort {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void recordRequest(String key, long timestamp) {
        // thêm timestamp mới vào đầu list (mới nhất luôn đứng đầu)
        // ví dụ: [2000, 1500, 1000]
        redisTemplate.opsForList().leftPush(key, String.valueOf(timestamp));

        // giữ tối đa 20 phần tử đầu tiên (20 request gần nhất)
        // tránh list dài vô hạn gây tốn memory
        redisTemplate.opsForList().trim(key, 0, 20);
        redisTemplate.expire(key, Duration.ofMinutes(1));
    }

    @Override
    public List<Long> getRecentTimestamps(String key, int limit) {

        // lấy các timestamp từ index 0 → limit (lưu ý: Redis range là inclusive)
        // ví dụ: limit = 10 → thực ra lấy 11 phần tử
        List<String> values = redisTemplate.opsForList().range(key, 0, limit);

        // nếu chưa có data thì trả về list rỗng
        if (values == null) return List.of();

        // convert từ String (Redis lưu string) → Long để xử lý tính toán
        return values.stream().map(Long::parseLong).toList();
    }
}
