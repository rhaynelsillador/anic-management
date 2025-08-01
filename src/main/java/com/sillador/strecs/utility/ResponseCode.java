package com.sillador.strecs.utility;

import lombok.Getter;

@Getter
public enum ResponseCode {

    SUCCESS(20),
    EXISTS(21),
    NOT_EXISTS(22),
    DUPLICATE(23),
    ERROR(50),
    INTERNAL_SERVER_ERROR(55), NOT_FOUND(24);

    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }

}
