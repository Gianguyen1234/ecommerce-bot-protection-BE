package com.holydev.platform.botprotection.domain.rule;


import com.holydev.platform.botprotection.domain.model.RequestContext;

public interface DetectionRule {

    RuleResult evaluate(RequestContext context);

}
