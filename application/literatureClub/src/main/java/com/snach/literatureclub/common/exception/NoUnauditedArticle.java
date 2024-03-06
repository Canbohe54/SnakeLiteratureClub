package com.snach.literatureclub.common.exception;

public class NoUnauditedArticle extends Exception {
    public NoUnauditedArticle() {
        super("No Unaudited Article.");
    }

    public NoUnauditedArticle(String message) {
        super(message);
    }
}
