package com.sillador.strecs.enums;

import lombok.Getter;

public enum GradingPeriod {

    FIRST(1, "First Quarter"),
    SECOND(2, "Second Quarter"),
    THIRD(3, "Third Quarter"),
    FOURTH(4, "Fourth Quarter"),
    FINAL(5, "Final Quarter");

    @Getter
    private final int code;
    @Getter
    private final String name;
    GradingPeriod(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
