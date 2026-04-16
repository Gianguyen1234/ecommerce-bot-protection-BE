package com.holydev.platform.botprotection.domain.rule;

import com.holydev.platform.botprotection.domain.model.RequestContext;

public class SuspiciousHeaderRule implements DetectionRule {

    @Override
    public RuleResult evaluate(RequestContext context) {
        String ua = context.getUserAgent();

        if (ua == null || ua.isBlank()) {
            return RuleResult.builder()
                    .score(40)
                    .reason("Missing User-Agent")
                    .build();
        }

        return RuleResult.builder()
                .score(0)
                .reason("OK")
                .build();
    }
}