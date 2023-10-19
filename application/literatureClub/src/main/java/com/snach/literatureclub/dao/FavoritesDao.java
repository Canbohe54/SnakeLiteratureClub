package com.snach.literatureclub.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FavoritesDao {

    /**
     * 用户收藏
     *
     * @param user_id    用户id
     * @param article_id 文章id
     */
    @Insert("INSERT INTO favorites(user_id,article_id) VALUES (#{user_id}, #{article_id})")
    void addFavorite(@Param("user_id") String user_id, @Param("article_id") String article_id);

    /**
     * 用户取消收藏
     *
     * @param user_id    用户id
     * @param article_id 文章id
     */
    @Delete("DELETE FROM favorites WHERE user_id = #{user_id} AND article_id = #{article_id}")
    void cancelFavorite(@Param("user_id") String user_id, @Param("article_id") String article_id);
}
