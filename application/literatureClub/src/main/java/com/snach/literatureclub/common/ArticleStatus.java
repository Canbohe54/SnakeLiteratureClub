package com.snach.literatureclub.common;

import java.util.Calendar;
import java.util.Date;

public enum ArticleStatus {
    // 草稿状态，未提交
    Rough,
    // 已提交，未审核
    Submitted,
    // 审核不通过
    FailAudited,
    // 审核通过，发布
    Published,
    Locked;
    private int expire = -1;

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public boolean checkExpire(Date time) {
        if (expire == -1) {
            return false;
        }
        time.setMinutes(time.getMinutes() + expire);
        return new Date().after(time);
    }

    public static int value(ArticleStatus status) {
        return status.ordinal();
    }

    public static ArticleStatus conv(int status) {
        return ArticleStatus.values()[status];
    }
}
