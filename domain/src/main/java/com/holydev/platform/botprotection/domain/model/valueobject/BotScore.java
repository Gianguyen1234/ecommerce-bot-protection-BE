package com.holydev.platform.botprotection.domain.model.valueobject;

import lombok.Value;

@Value
public class BotScore {
    int value;

    public BotScore add(int delta) {
        return new BotScore(this.value + delta);
    }
}
