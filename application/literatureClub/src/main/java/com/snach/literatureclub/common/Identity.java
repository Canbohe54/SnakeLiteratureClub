package com.snach.literatureclub.common;

import java.util.Map;

public enum Identity {
    Visitor,
    Contributor,
    Teacher,
    Expert,
    Hunter,
    Manager,
    Auditor;

    public static String value(Identity identity) {
        return switch (identity) {
            case Contributor -> "学生";
            case Expert -> "专家";
            case Auditor -> "审核";
            default -> "";
        };
    }

    public static Identity conv(String identity) {
        return switch (identity) {
            case "学生" -> Contributor;
            case "专家" -> Expert;
            case "审核" -> Auditor;
            default -> Visitor;
        };
    }
}
