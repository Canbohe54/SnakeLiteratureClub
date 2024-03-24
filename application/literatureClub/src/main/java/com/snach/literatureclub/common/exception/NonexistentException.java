package com.snach.literatureclub.common.exception;

public class NonexistentException extends RuntimeException {
    public NonexistentException() {
        super("Nonexistent.");
    }

    public NonexistentException(String nonexistentObjectName) {
        super("Nonexistent " + nonexistentObjectName);
    }

    public NonexistentException(Class<?> clazz) {
        super("Nonexistent " + clazz.getName());
    }
}
