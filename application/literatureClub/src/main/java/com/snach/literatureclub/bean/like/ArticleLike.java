package com.snach.literatureclub.bean.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLike {
    private String like_id;
    private String article_id; // 文章id
    private String user_id; // 用户id
    private Integer status; // 状态 0 取消 1 点赞

    public ArticleLike(String articleId, String likeUserId, Integer status) {
        this.article_id = articleId;
        this.user_id = likeUserId;
        this.status = status;
    }
}
