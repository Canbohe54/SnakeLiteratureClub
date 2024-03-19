package com.snach.literatureclub.common.exception;

public class NonexistentUserException extends RuntimeException {
    public NonexistentUserException() {
        super("Nonexistent User.");
    }

    public NonexistentUserException(String message) {
        super(message);
    }
}
