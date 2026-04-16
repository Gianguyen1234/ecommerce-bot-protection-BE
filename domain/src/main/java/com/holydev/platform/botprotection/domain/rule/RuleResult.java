package com.holydev.platform.botprotection.domain.rule;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RuleResult {
    int score;
    String reason;
}