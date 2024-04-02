package com.snach.literatureclub.utils;

import java.util.List;

public class SqlQueryStatement {
    public static String toSelectString(List<?> objectList) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < objectList.size(); i++) {
            sb.append("\"").append(objectList.get(i).toString()).append("\"");
            if (i != objectList.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
