package com.ssafyhome.deal.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum dealType {
    TRADE, RENT;

    @JsonCreator
    public static dealType fromString(String key) {
        return key == null ? null : dealType.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
