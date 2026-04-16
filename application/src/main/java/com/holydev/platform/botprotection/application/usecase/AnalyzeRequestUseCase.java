package com.holydev.platform.botprotection.application.usecase;


import com.holydev.platform.botprotection.domain.decision.DetectionDecision;
import com.holydev.platform.botprotection.domain.model.RequestContext;

public interface AnalyzeRequestUseCase {

    DetectionDecision analyze(RequestContext context);

}
