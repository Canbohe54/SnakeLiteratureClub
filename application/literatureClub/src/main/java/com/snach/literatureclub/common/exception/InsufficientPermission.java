package com.snach.literatureclub.common.exception;

public class InsufficientPermission extends Exception {
    public InsufficientPermission() {
        super("Insufficient Permission.");
    }

    public InsufficientPermission(String message) {
        super(message);
    }
}
