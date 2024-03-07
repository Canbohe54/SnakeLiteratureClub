package com.snach.literatureclub.common.exception;

public class NoUnauditedArticleException extends RuntimeException {
    public NoUnauditedArticleException() {
        super("No Unaudited Article.");
    }

    public NoUnauditedArticleException(String message) {
        super(message);
    }
}
