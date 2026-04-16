package com.holydev.platform.botprotection.domain.rule;

import com.holydev.platform.botprotection.domain.model.RequestContext;
import com.holydev.platform.botprotection.domain.port.RequestHistoryPort;
import com.holydev.platform.botprotection.domain.service.FingerprintGenerator;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class IntervalVarianceRule implements DetectionRule {

    // nơi lưu lịch sử request (có thể là Redis, DB, memory...)
    private final RequestHistoryPort historyPort;

    // tạo fingerprint để phân biệt từng user/client
    private final FingerprintGenerator fingerprintGenerator;

    @Override
    public RuleResult evaluate(RequestContext context) {

        // tạo fingerprint từ request (IP, header, device...)
        var fp = fingerprintGenerator.generate(context);
        String key = "interval:" + fp.getHash();

        long now = System.currentTimeMillis();

        historyPort.recordRequest(key, now);

        // lay 10 requests gần nhất
        var timestamps = historyPort.getRecentTimestamps(key, 10);

        // nếu chưa đủ data thì bỏ qua (tránh detect sai)
        if (timestamps.size() < 5) {
            return RuleResult.builder().score(0).reason("Not enough data").build();
        }

        var intervals = new ArrayList<Long>();

        // tính khoảng cách giữa các request liên tiếp
        // ví dụ:
        // timestamps: [1000, 1500, 2000]
        // intervals:  [500, 500]
        for (int i = 1; i < timestamps.size(); i++) {
            //tính khoảng cách giữa các request
            intervals.add(Math.abs(timestamps.get(i) - timestamps.get(i - 1)));
        }

        // tính độ "đều" của các khoảng cách
        // variance thấp = các khoảng cách gần như giống nhau
        double variance = calculateVariance(intervals);

        // variance thấp → đều → bot (vì bot thường chạy loop delay cố định)
        if (variance < 50) {
            return RuleResult.builder()
                    .score(15) // cộng điểm nghi ngờ, chưa block ngay
                    .reason("Low interval variance (bot-like)")
                    .build();
        }

        // bình thường → coi như user hợp lệ
        return RuleResult.builder().score(0).reason("OK").build();
    }

    private double calculateVariance(List<Long> values) {
        // tính trung bình các khoảng cách
        // ví dụ: [500, 500, 500] → mean = 500
        double mean = values.stream()
                .mapToDouble(Long::doubleValue)
                .average()
                .orElse(0);

        // tính variance:
        // đo xem mỗi giá trị lệch khỏi mean bao nhiêu
        // (v - mean)^2 càng nhỏ → càng đều
        return values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0);
    }
}
