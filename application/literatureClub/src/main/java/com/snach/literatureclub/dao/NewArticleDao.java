package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.ArticleStatus;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface NewArticleDao {
    // SELECT-by-condition
    @Select("SELECT * FROM article WHERE id = #{id}")
    Article getArticleById(String id);

    @Select("SELECT * FROM article WHERE status = #{status} LIMIT 1")
    Article getArticleByStatus(ArticleStatus status);

    // INSERT //
    @Insert("INSERT INTO article(id, title, description, text, time, status, attr, text_by, image_url, raw, mentor) " +
            "VALUES (#{article.id}, )")
    void insertArticle(Article article);
}
