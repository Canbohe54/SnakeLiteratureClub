package com.snach.literatureclub.common;

public enum ArticleAuditStatus {
    // 草稿状态，未提交
    ROUGH,
    // 已提交，未审核
    SUBMITTED,
    // 审核不通过
    FAIL_AUDITED,
    // 正在审核
    BEING_AUDITED,
    // 审核通过，发布
    AUDITED,
    LOCKED
}
