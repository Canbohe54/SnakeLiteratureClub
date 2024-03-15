package com.snach.literatureclub.common.exception;

public class NullFileException extends RuntimeException{
    public NullFileException() {
        super("File is null.");
    }

    public NullFileException(String message) {
        super(message);
    }
}
