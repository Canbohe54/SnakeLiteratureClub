package com.snach.literatureclub.utils;

import java.util.Random;

public class Tools {
    private static final String code = "WLJwjm8F6iySOH2aXvK4RnZ1Nkbel93cQoYgzGE0rChVIMdDTPs5UAfqt7uBxp";
    private static final Random random = new Random();

    public enum Type {
        ARTICLE,
        COMMENT,
        USER,
        NONE,
    }

    public static Type idType(String id) {
        if (id.charAt(0) == 'a') {
            return Type.ARTICLE;
        } else if (id.charAt(0) == 'c') {
            return Type.COMMENT;
        } else if (id.charAt(0) == 'u') {
            return Type.USER;
        } else {
            return Type.NONE;
        }
    }

    private synchronized static String randomStringGen(int length) {
        StringBuilder randStr = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randStr.append(code.charAt(random.nextInt(code.length())));
        }
        return randStr.toString();
    }

    public static String numberConvert(long timeStamp) {
        StringBuilder stamp = new StringBuilder();
        while (timeStamp >= code.length()) {
            stamp.append(code.charAt((int) (timeStamp % code.length())));
            timeStamp = timeStamp / code.length();
        }
        stamp.append(code.charAt((int) (timeStamp % code.length())));
        stamp.reverse();
        stamp.append(randomStringGen(4));
        return stamp.toString();
    }

    private static String generateTextId(String head) {
        return head + numberConvert(System.currentTimeMillis());
    }

    private static String generateUserId() {
        return null;
    }

    public static String generateId(Type type) {
        if (type == Type.COMMENT) {
            return generateTextId("c");
        } else if (type == Type.ARTICLE) {
            return generateTextId("a");
        } else if (type == Type.USER) {
            return generateUserId();
        }
        return null;
    }
}
