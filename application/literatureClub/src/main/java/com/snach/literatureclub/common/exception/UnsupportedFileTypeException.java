package com.snach.literatureclub.common.exception;

public class UnsupportedFileTypeException extends RuntimeException {
    public UnsupportedFileTypeException() {
        super("Unsupported file type.");
    }

    public UnsupportedFileTypeException(String expectedFileTypes) {
        super("Unsupported file type. Expect: " + expectedFileTypes + ".");
    }
}
