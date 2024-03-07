package com.snach.literatureclub.common.exception;

public class InsufficientPermissionException extends RuntimeException {
    public InsufficientPermissionException() {
        super("Insufficient Permission.");
    }

    public InsufficientPermissionException(String message) {
        super(message);
    }
}

