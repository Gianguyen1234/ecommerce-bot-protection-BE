package com.holydev.platform.botprotection.domain.port;

public interface RequestMetricsPort {

    /**
     * @return số request trong window
     */
    long countRequests(String key, long windowSeconds);

}
