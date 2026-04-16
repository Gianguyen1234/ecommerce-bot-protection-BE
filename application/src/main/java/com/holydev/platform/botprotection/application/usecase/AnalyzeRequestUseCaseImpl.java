package com.holydev.platform.botprotection.application.usecase;

import com.holydev.platform.botprotection.domain.decision.DetectionDecision;
import com.holydev.platform.botprotection.domain.model.RequestContext;
import com.holydev.platform.botprotection.domain.port.SuspicionPort;
import com.holydev.platform.botprotection.domain.rule.RuleEngine;
import com.holydev.platform.botprotection.domain.service.DecisionPolicy;
import com.holydev.platform.botprotection.domain.service.FingerprintGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AnalyzeRequestUseCaseImpl implements AnalyzeRequestUseCase {

    private final RuleEngine ruleEngine;
    private final DecisionPolicy decisionPolicy;
    private final SuspicionPort suspicionPort;
    private final FingerprintGenerator fingerprintGenerator;

    @Override
    public DetectionDecision analyze(RequestContext context) {

        // 1. generate fingerprint
        var fingerprint = fingerprintGenerator.generate(context);
        String key = "sus:" + fingerprint.getHash();

        // 2. evaluate rules
        var result = ruleEngine.evaluate(context);

        // 3. kiểm tra suspicion TRƯỚC
        boolean suspicious = suspicionPort.isSuspicious(key);

        // 4. LOG ở đây
        System.out.println(
                "Score=" + result.getScore().getValue() +
                        " triggeredRules=" + result.getReasons().size() +
                        " suspicious=" + suspicious +
                        " reasons=" + result.getReasons()
        );

        // 5. nếu score cao → mark suspicious
        if (result.getScore().getValue() >= 50) {
            suspicionPort.markSuspicious(key);
        }

        // 6. nếu đã bị đánh dấu → override decision
        if (suspicious) {
            return DetectionDecision.CHALLENGE;
        }

        // 7. fallback decision
        return decisionPolicy.decide(result.getScore());
    }
}

//@Service
//@RequiredArgsConstructor
//public class AnalyzeRequestUseCaseImpl implements AnalyzeRequestUseCase {
//
//    private final RuleEngine ruleEngine;
//    private final DecisionPolicy decisionPolicy;
//
//    @Override
//    public DetectionDecision analyze(RequestContext context) {
//
//        var result = ruleEngine.evaluate(context);
//
//        System.out.println(
//                "Score=" + result.getScore().getValue() +
//                        " triggeredRules=" + result.getReasons().size() +
//                        " reasons=" + result.getReasons()
//        );
//
//        return decisionPolicy.decide(result.getScore());
//    }
//}
