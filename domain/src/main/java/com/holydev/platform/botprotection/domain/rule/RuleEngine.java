package com.holydev.platform.botprotection.domain.rule;

import com.holydev.platform.botprotection.domain.model.RequestContext;
import com.holydev.platform.botprotection.domain.model.valueobject.BotScore;

import java.util.ArrayList;
import java.util.List;

public class RuleEngine {

    // danh sách tất cả rule (HighFrequency, IntervalVariance, PathSequence...)
    private final List<DetectionRule> rules;

    public RuleEngine(List<DetectionRule> rules) {
        this.rules = rules;
    }

    public EvaluationResult evaluate(RequestContext context) {

        int totalScore = 0; // tổng điểm nghi bot
        int triggeredRules = 0;

        List<String> reasons = new ArrayList<>(); // lưu lý do để debug/log

        // chạy từng rule một
        for (DetectionRule rule : rules) {

            // mỗi rule tự đánh giá request và trả về kết quả
            RuleResult result = rule.evaluate(context);

            // cộng dồn điểm
            totalScore += result.getScore();

            // nếu rule phát hiện bất thường (score > 0)
            if (result.getScore() > 0) {

                // tăng số rule đã trigger
                triggeredRules++;

                // lưu lại lý do (ví dụ: "High frequency", "Low variance")
                reasons.add(result.getReason());
            }
        }

        // nếu chỉ có 0 hoặc 1 rule nghi ngờ → KHÔNG đủ tin cậy
        // (tránh false positive)
        if (triggeredRules < 2) {
            return new EvaluationResult(
                    new BotScore(0), // reset về 0, coi như user bình thường
                    reasons
            );
        }

        // nếu >= 2 rule cùng nghi ngờ → đáng tin là bot
        return new EvaluationResult(
                new BotScore(totalScore), // giữ nguyên tổng điểm
                reasons
        );
    }
}
