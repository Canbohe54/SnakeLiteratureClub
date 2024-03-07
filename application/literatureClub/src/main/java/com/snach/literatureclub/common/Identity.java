package com.snach.literatureclub.common;

public enum Identity {
    VISITOR,
    CONTRIBUTOR,
    TEACHER,
    EXPERT,
    HUNTER,
    MANAGER,
    AUDITOR;

    public static String value(Identity identity) {
        return switch (identity) {
            case CONTRIBUTOR -> "学生";
            case EXPERT -> "专家";
            case AUDITOR -> "审核";
            default -> "";
        };
    }

    public static Identity conv(String identity) {
        return switch (identity) {
            case "学生" -> CONTRIBUTOR;
            case "专家" -> EXPERT;
            case "审核" -> AUDITOR;
            default -> VISITOR;
        };
    }
}
