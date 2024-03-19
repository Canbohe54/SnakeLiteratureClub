package com.snach.literatureclub.utils;

public class IdManager {
    private static long nextUserId;

    static {
        //TODO: 获取上一次的id数据
        nextUserId = 10001;
    }

    private IdManager() {}

    public static synchronized String generateUserId() {
        String newId = String.valueOf(nextUserId);
        nextUserId++;
        if (1e11 <= nextUserId && nextUserId < 2e11) {
            nextUserId = (long) 2e11;
        }
        return newId;
    }

    public static synchronized String generateArticleId() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static synchronized String generateCommentId() {
        return String.valueOf(System.currentTimeMillis());
    }
}
