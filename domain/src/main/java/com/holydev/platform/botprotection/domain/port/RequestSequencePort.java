package com.holydev.platform.botprotection.domain.port;

import java.util.List;

public interface RequestSequencePort {

    void recordPath(String key, String path);

    List<String> getRecentPaths(String key, int limit);
}
