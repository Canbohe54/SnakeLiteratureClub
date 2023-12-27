package com.snach.literatureclub.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 根据用户id查找其收藏稿件id
     * @param user_id 用户id
     * @return 稿件id列表
     */
    @Select("SELECT article_id FROM favorites WHERE user_id = #{user_id}")
    List<String> getAIdByUId(String user_id);

    /**
    * 根据用户id和稿件id，搜索其是否为收藏稿件
    * @param user_id 用户id
    * @param article_id 文章id
    * @return 若无则为空
    * */
    @Select(value = "SELECT COUNT(*) FROM favorites WHERE user_id = #{user_id} AND article_id = #{article_id}")
    int isArticleFavorited(String user_id, String article_id);
}
