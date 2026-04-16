package com.holydev.platform.botprotection.domain.rule;

import com.holydev.platform.botprotection.domain.model.valueobject.BotScore;
import lombok.Value;

import java.util.List;

@Value
public class EvaluationResult {
    BotScore score;
    List<String> reasons;
}