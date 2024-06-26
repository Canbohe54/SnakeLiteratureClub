package com.snach.literatureclub.common;

public enum ArticlePublishStatus {
    // 公开
    PUBLIC,

    // 推荐审阅中
    UNDER_REVIEW,

    // 收录审阅中
    UNDER_RECORD,

    // 预收录，但未刊登
    POST_RECORD,

    // 用户同意收录
    POSTED,

    // 推荐未通过
    FAILED_REVIEW,

    // 不收录
    FAILED_RECORD
}
