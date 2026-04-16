package com.holydev.platform.botprotection.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Fingerprint {
    String hash;
}