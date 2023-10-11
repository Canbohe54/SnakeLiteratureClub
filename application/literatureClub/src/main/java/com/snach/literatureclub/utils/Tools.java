package com.snach.literatureclub.utils;

import java.math.BigInteger;
import java.text.SimpleDateFormat;

public class Tools {
    public static String generateId(String type) {
        //时间戳生成id简单实现，以后可改用雪花算法
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        return type+simpleDateFormat.format(System.currentTimeMillis());
    }
}
