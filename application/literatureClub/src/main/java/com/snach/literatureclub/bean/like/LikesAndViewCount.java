package com.snach.literatureclub.bean.like;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikesAndViewCount {
    private String article_id; // 文章id
    private Integer like_count; // 点赞数
    private Integer view_count; // 浏览量
    public LikesAndViewCount(String article_id, Integer like_count) {
        this.article_id = article_id;
        this.like_count = like_count;
    }

}
