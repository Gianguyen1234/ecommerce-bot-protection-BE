package com.holydev.platform.botprotection.domain.rule;

import com.holydev.platform.botprotection.domain.model.RequestContext;
import com.holydev.platform.botprotection.domain.port.RequestMetricsPort;
import com.holydev.platform.botprotection.domain.service.FingerprintGenerator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HighFrequencyRule implements DetectionRule {

    private final RequestMetricsPort metricsPort;
    private final FingerprintGenerator fingerprintGenerator;

    private final int threshold;        // ví dụ: 20 request
    private final long windowSeconds;   // ví dụ: 10s

    @Override
    public RuleResult evaluate(RequestContext context) {

        String key = buildKey(context);

        long count = metricsPort.countRequests(key, windowSeconds);

        if (count > threshold) {
            return RuleResult.builder()
                    .score(60)
                    .reason("High frequency: " + count)
                    .build();
        }

        return RuleResult.builder().score(0).reason("OK").build();
    }

    private String buildKey(RequestContext context) {
        var fp = fingerprintGenerator.generate(context);
        return "rate:" + fp.getHash();
    }
}