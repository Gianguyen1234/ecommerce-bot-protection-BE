package com.holydev.platform.botprotection.api.config;

import com.holydev.platform.botprotection.domain.port.RequestHistoryPort;
import com.holydev.platform.botprotection.domain.port.RequestMetricsPort;
import com.holydev.platform.botprotection.domain.port.RequestSequencePort;
import com.holydev.platform.botprotection.domain.rule.*;
import com.holydev.platform.botprotection.domain.service.DecisionPolicy;
import com.holydev.platform.botprotection.domain.service.DefaultFingerprintGenerator;
import com.holydev.platform.botprotection.domain.service.FingerprintGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanConfig {

    // =========================
    // Fingerprint
    // =========================
    @Bean
    public FingerprintGenerator fingerprintGenerator() {
        return new DefaultFingerprintGenerator();
    }

    // =========================
    // Rules
    // =========================

    @Bean
    public HighFrequencyRule highFrequencyRule(
            RequestMetricsPort metricsPort,
            FingerprintGenerator fingerprintGenerator
    ) {
        return new HighFrequencyRule(metricsPort, fingerprintGenerator, 5, 60);
    }

    @Bean
    public IntervalVarianceRule intervalVarianceRule(
            RequestHistoryPort historyPort,
            FingerprintGenerator fingerprintGenerator
    ) {
        return new IntervalVarianceRule(historyPort, fingerprintGenerator);
    }

    @Bean
    public PathSequenceRule pathSequenceRule(
            RequestSequencePort sequencePort,
            FingerprintGenerator fingerprintGenerator
    ) {
        return new PathSequenceRule(sequencePort, fingerprintGenerator);
    }

    @Bean
    public SuspiciousHeaderRule suspiciousHeaderRule() {
        return new SuspiciousHeaderRule();
    }

    // =========================
    // Rule Engine
    // =========================
    @Bean
    public RuleEngine ruleEngine(
            HighFrequencyRule highFrequencyRule,
            IntervalVarianceRule intervalVarianceRule,
            PathSequenceRule pathSequenceRule,
            SuspiciousHeaderRule suspiciousHeaderRule
    ) {
        return new RuleEngine(List.of(
                highFrequencyRule,
                intervalVarianceRule,
                pathSequenceRule,
                suspiciousHeaderRule
        ));
    }

    // =========================
    // Decision
    // =========================
    @Bean
    public DecisionPolicy decisionPolicy() {
        return new DecisionPolicy();
    }
}
