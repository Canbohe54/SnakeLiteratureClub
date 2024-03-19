package com.snach.literatureclub.common.exception;

public class WrongIdOrPasswordException extends RuntimeException {
    public WrongIdOrPasswordException() {
        super("Wrong Id Or Password.");
    }

    public WrongIdOrPasswordException(String message) {
        super(message);
    }
}
