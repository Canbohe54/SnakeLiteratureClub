package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.like.LikesAndViewCount;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ArticleLikeAndViewCountDao {
    @Insert("INSERT INTO likes_views_count(article_id, like_count, view_count) VALUES(#{article_id}, #{like_count}, #{view_count})")
    int insertLikeAndViewCount(LikesAndViewCount likesAndViewCount);

    @Update("UPDATE likes_views_count SET like_count = #{like_count} WHERE article_id = #{article_id}")
    int updateLikeCount(String article_id, Integer like_count);

    @Update("UPDATE likes_views_count SET view_count = #{view_count} WHERE article_id = #{article_id}")
    int updateViewCount(String article_id, Integer view_count);

    @Insert("INSERT INTO likes_views_count(article_id, view_count) VALUES(#{article_id}, #{view_count})")
    int insertView(String article_id, Integer view_count);

    @Update("UPDATE likes_views_count SET view_count = #{view_count} WHERE article_id = #{article_id}")
    int updateView(String article_id, Integer view_count);

    @Select("SELECT * FROM likes_views_count WHERE article_id = #{article_id}")
    LikesAndViewCount getByArticleId(String article_id);
}
