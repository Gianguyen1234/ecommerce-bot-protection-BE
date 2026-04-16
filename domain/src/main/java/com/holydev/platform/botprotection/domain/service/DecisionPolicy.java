package com.holydev.platform.botprotection.domain.service;


import com.holydev.platform.botprotection.domain.decision.DetectionDecision;
import com.holydev.platform.botprotection.domain.model.valueobject.BotScore;

public class DecisionPolicy {

    public DetectionDecision decide(BotScore score) {

        if (score.getValue() >= 80) {
            return DetectionDecision.BLOCK;
        }

        if (score.getValue() >= 50) {
            return DetectionDecision.CHALLENGE;
        }

        return DetectionDecision.ALLOW;
    }
}
