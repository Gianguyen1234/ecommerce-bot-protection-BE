package com.holydev.platform.botprotection.domain.model;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
// business input model
public class RequestContext {
    String ip;
    String userAgent;
    String path;
    String method;
    Map<String, String> headers;
    long timestamp;
}
