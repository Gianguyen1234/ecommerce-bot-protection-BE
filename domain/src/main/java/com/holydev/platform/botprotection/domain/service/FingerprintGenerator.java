package com.holydev.platform.botprotection.domain.service;

import com.holydev.platform.botprotection.domain.model.Fingerprint;
import com.holydev.platform.botprotection.domain.model.RequestContext;

public interface FingerprintGenerator {

    Fingerprint generate(RequestContext context);

}