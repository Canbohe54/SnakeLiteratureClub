package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.like.ArticleLike;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@Mapper
public interface ArticleLikeDao {
    @Insert("INSERT INTO article_likes(like_id,article_id, user_id, status) VALUES(#{like_id},#{article_id}, #{user_id}, #{status})")
    int insert(ArticleLike articleLike);

    @Update("UPDATE article_likes SET status = #{status} WHERE article_id = #{article_id} AND user_id = #{user_id}")
    int update(String article_id, String user_id,Integer status);

    @Select("SELECT * FROM article_likes WHERE article_id = #{article_id} AND user_id = #{user_id}")
    ArticleLike getByArticleIdAndLikeUserId(String article_id, String user_id);

    @Select("SELECT * FROM article_likes WHERE article_id = #{articleId}")
    List<ArticleLike> getLikedListByArticleId(String articleId);

    @Select("SELECT * FROM article_likes WHERE user_id = #{userId}")
    List<ArticleLike> getLikedListByLikeUserId (String userId);
}
