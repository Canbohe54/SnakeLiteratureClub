package com.snach.literatureclub.common;

import lombok.Getter;

import java.util.Date;
@Getter
public enum ArticleStatus {
    // 草稿状态，未提交
    ROUGH("rough"),
    // 已提交，未审核
    SUBMITTED("submitted"),
    // 审核不通过
    FAIL_AUDITED("failAudited"),
    // 审核通过，发布
    PUBLISHED("published"),
    LOCKED("locked");

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;
    private int expire = -1;
    ArticleStatus(String status){
        this.status = status;
    }
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

    public static ArticleStatus conv(String status) {
        return ArticleStatus.values()[Integer.getInteger(status)];
    }
}
