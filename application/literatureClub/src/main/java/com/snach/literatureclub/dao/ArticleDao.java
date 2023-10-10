package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.Article;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@Mapper
public interface ArticleDao {
    /**
     * 插入方法
     * @param article 封装有稿件信息的Article对象
     *
     */
    @Insert("INSERT INTO article(id, title,description,text,time) VALUES(#{article.id}, #{article.title},#{article.description},#{article.time})")
    void insertArticle(@Param("article") Article article);

    /**
     * 更新稿件详细信息,包括包括标题、描述和内容
     * @param article 封装有稿件信息的Article对象
     * @return 匹配到的行数（如果想设置返回值是受影响的行数，修改数据库链接配置：增加 useAffectedRows=true 即可）
     */
    @Update("UPDATE article SET title = #{article.title}, description = #{article.description}, text = #{article.text}, time = #{article.time} WHERE id = #{article.id}")
    int updateArticleDetail(@Param("article") Article article);

    /**
     * 更新稿件基础信息,包括包括标题和描述
     * @param article 封装有稿件信息的Article对象
     * @return 匹配到的行数（如果想设置返回值是受影响的行数，修改数据库链接配置：增加 useAffectedRows=true 即可）
     */
    @Update("UPDATE article SET title = #{article.title}, description = #{article.description}, time = #{article.time} WHERE id = #{article.id}")
    int updateArticleInfo(@Param("article") Article article);
    /**
     * 根据稿件id进行删除
     * @param id 稿件id
     */
    @Delete("DELETE FROM article WHERE id = #{id}")
    void deleteArticleById(@Param("id") BigInteger id);

    /**
     * 根据作者id查找其稿件，返回稿件id、标题、描述和时间
     * @param contributor_id 作者id
     * @return
     */
    @Select("SELECT a.id id,a.title title,a.description description,a.time time" +
            "FROM article a left join contributor_article_list c on a.id = c.article_id " +
            "WHERE c.contributor_id = #{contributor_id}")
    Article getArticleByContributorId(@Param("contributor_id") BigInteger contributor_id);

    /**
     * 根据id获取单个稿件的详细信息
     * @param id 稿件id
     * @return id对应的稿件信息的Article对象
     */
    @Select("SELECT * FROM article WHERE id = #{id}")
    Article getArticleById(@Param("id") int id);

    /**
     * 获取所有的稿件信息
     * @return 所有的稿件信息的Article对象List
     */
    @Select("SELECT * FROM article")
    List<Article> getAllArticles();

}
