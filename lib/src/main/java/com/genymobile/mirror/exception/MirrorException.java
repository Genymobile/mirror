package com.genymobile.mirror.exception;

public class MirrorException extends RuntimeException {

    public MirrorException(String message) {
        super(message);
    }

    public MirrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
