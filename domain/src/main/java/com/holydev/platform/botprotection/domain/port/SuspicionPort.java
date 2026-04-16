package com.holydev.platform.botprotection.domain.port;

public interface SuspicionPort {

    void markSuspicious(String key);

    boolean isSuspicious(String key);

}
