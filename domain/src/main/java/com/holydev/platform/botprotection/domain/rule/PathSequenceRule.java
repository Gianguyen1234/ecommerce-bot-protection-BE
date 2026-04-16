package com.holydev.platform.botprotection.domain.rule;

import com.holydev.platform.botprotection.domain.model.RequestContext;
import com.holydev.platform.botprotection.domain.port.RequestSequencePort;
import com.holydev.platform.botprotection.domain.service.FingerprintGenerator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PathSequenceRule implements DetectionRule {

    // nơi lưu lịch sử path (Redis / memory / DB)
    private final RequestSequencePort sequencePort;

    private final FingerprintGenerator fingerprintGenerator;

    @Override
    public RuleResult evaluate(RequestContext context) {

        // xác định "ai đang request"
        var fp = fingerprintGenerator.generate(context);
        String key = "seq:" + fp.getHash();

        // lấy path hiện tại và normalize lại
        // ví dụ: /product/123 → /product/:id
        String path = normalize(context.getPath());

        // lưu lại path này
        sequencePort.recordPath(key, path);

        // lấy 10 path gần nhất
        var paths = sequencePort.getRecentPaths(key, 10);

        // chưa đủ data thì bỏ qua
        if (paths.size() < 5) {
            return RuleResult.builder()
                    .score(0)
                    .reason("Not enough data")
                    .build();
        }

        // đếm số path khác nhau
        long unique = paths.stream().distinct().count();

        // nếu tất cả đều khác nhau → rất đáng nghi
        // vì user thật hiếm khi click 10 trang mà không trùng
        if (unique == paths.size()) {
            return RuleResult.builder()
                    .score(10)
                    .reason("Sequential scraping pattern")
                    .build();
        }

        return RuleResult.builder()
                .score(0)
                .reason("OK")
                .build();
    }

    private String normalize(String path) {
        // biến số ID thành dạng chung
        // /user/123 → /user/:id
        return path.replaceAll("/\\d+", "/:id");
    }
}