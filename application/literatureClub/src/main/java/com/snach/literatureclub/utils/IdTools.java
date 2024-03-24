package com.snach.literatureclub.utils;

import java.util.Random;

@Deprecated
public class IdTools {
    private static final String code62 = "WLJwjm8F6iySOH2aXvK4RnZ1Nkbel93cQoYgzGE0rChVIMdDTPs5UAfqt7uBxp";
    private static final String code10 = "0123456789";
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

    public synchronized static String randomStringGen62(int length) {
        StringBuilder randStr = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randStr.append(code62.charAt(random.nextInt(code62.length())));
        }
        return randStr.toString();
    }

    public synchronized static String randomStringGen10(int length) {
        StringBuilder randStr = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randStr.append(code10.charAt(random.nextInt(code10.length())));
        }
        return randStr.toString();
    }

    public static String numberConvert(long timeStamp) {
        StringBuilder stamp = new StringBuilder();
        while (timeStamp >= code62.length()) {
            stamp.append(code62.charAt((int) (timeStamp % code62.length())));
            timeStamp = timeStamp / code62.length();
        }
        stamp.append(code62.charAt((int) (timeStamp % code62.length())));
        stamp.append(randomStringGen62(4));
        return stamp.toString();
    }

    private static String generateTextId(String head) {
        return head + numberConvert(System.currentTimeMillis());
    }

    private static String generateUserId() {
        return "u" + numberConvert(System.currentTimeMillis());
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
