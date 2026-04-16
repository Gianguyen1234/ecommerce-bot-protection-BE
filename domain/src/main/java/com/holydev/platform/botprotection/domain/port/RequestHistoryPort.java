package com.holydev.platform.botprotection.domain.port;

import java.util.List;

public interface RequestHistoryPort {

    void recordRequest(String key, long timestamp);

    List<Long> getRecentTimestamps(String key, int limit);
}