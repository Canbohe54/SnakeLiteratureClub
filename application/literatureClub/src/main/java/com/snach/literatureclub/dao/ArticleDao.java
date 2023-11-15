package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.Article;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
@Mapper
public interface ArticleDao {
    /**
     * 插入方法
     *
     * @param article 封装有稿件信息的Article对象
     */
    @Insert("INSERT INTO article(id, title,description,text,`time`,status,attr,text_by) " +
            "VALUES(#{article.id}, #{article.title},#{article.description}, #{article.text}, #{article.time},#{article.status},#{article.attr},#{article.textBy})")
    void insertArticle(@Param("article") Article article);

    /**
     * 更新稿件详细信息,包括包括标题、描述和内容
     *
     * @param article 封装有稿件信息的Article对象
     * @return 匹配到的行数（如果想设置返回值是受影响的行数，修改数据库链接配置：增加 useAffectedRows=true 即可）
     */
    @Update("UPDATE article SET title = #{article.title}, description = #{article.description}, text = #{article.text}, time = #{article.time},status = #{article.status}, attr = #{article.attr} " +
            "WHERE id = #{article.id}")
    int updateArticleDetail(@Param("article") Article article);

    /**
     * 更新稿件基础信息,包括包括标题和描述
     *
     * @param article 封装有稿件信息的Article对象
     * @return 匹配到的行数（如果想设置返回值是受影响的行数，修改数据库链接配置：增加 useAffectedRows=true 即可）
     */
    @Update("UPDATE article SET title = #{article.title}, description = #{article.description}, time = #{article.time},status = #{article.status}, attr = #{article.attr} " +
            "WHERE id = #{article.id}")
    int updateArticleInfo(@Param("article") Article article);

    /**
     * 根据稿件id进行删除
     *
     * @param id 稿件id
     */
    @Delete("DELETE FROM article WHERE id = #{id}")
    void deleteArticleById(@Param("id") String id);

    /**
     * 根据作者id查找其稿件，返回稿件id、标题、描述和时间
     *
     * @param contributor_id 作者id
     * @return
     */
    @Select("SELECT a.id id,a.title title,a.description description,a.time time,a.status status, a.attr attr " +
            "FROM article a left join contributor_article_list c on a.id = c.article_id " +
            "WHERE c.contributor_id = #{contributor_id}")
    List<Article> getArticleByContributorId(@Param("contributor_id") String contributor_id);

    /**
     * 根据稿件id获取单个稿件的基础信息
     *
     * @param article_id 稿件id
     * @return id对应的稿件信息的Article对象
     */
    @Select("SELECT a.id id,a.title title,a.description description,a.time time,a.status status, a.attr attr FROM article a WHERE id = #{id}")
    Article getArticleBasicById(@Param("id") String article_id);

    /**
     * 根据id获取单个稿件的详细信息
     *
     * @param id 稿件id
     * @return id对应的稿件信息的Article对象
     */
    @Select("SELECT id, text, time, text_by, title, description, status, attr FROM article WHERE id = #{id}")
    Article getArticleById(@Param("id") String id);

    /**
     * 获取所有的稿件信息
     *
     * @return 所有的稿件信息的Article对象List
     */
    @Select("SELECT id, time, text_by as textBy, title, description, status, attr FROM article")
    List<Article> getAllArticles();

    /**
     * 根据名称关键字和标签获取稿件
     *
     * @param keyword 关键字
     * @param tag     标签
     * @return 所有符合条件的稿件
     */
    @Select("SELECT id, text, time, text_by, title, description, status, attr FROM article WHERE title LIKE '%${keyword}%' AND attr LIKE '%\"tags\":%\"${tag}\"%]%' AND status=3")
    List<Article> getArticlesByKeywordAndTag(String keyword, String tag);

    /**
     * 根据名称关键字获取稿件
     *
     * @param keyword 关键字
     * @return 所有符合条件的稿件
     */
    @Select("SELECT id, text, time, text_by as textBy, title, description, status, attr FROM article WHERE title LIKE '%${keyword}%' AND status=3")
    List<Article> getArticlesByKeyword(String keyword);

    /**
     * 插入作者与稿件关系
     *
     * @param contributor_id 作者id
     * @param article_id     稿件id
     */
    @Insert("INSERT INTO contributor_article_list(contributor_id,article_id) VALUES(#{c_id},#{a_id})")
    void insertRelation(@Param("c_id") String contributor_id, @Param("a_id") String article_id);

    /**
     * 更新稿件保存状态
     *
     * @param status 稿件状态 （1：保存成功 2：待审核 3.已发布 4.未通过 0：保存失败）
     * @param id     文章id
     * @return
     */
    @Update("UPDATE article SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("status") int status, @Param("id") String id);

    /**
     * 判断稿件是否属于该作者
     *
     * @param contributor_id 作者id
     * @param article_id     稿件id
     * @return 匹配到的行数
     */
    @Select("Select COUNT(*) FROM contributor_article_list WHERE contributor_id = #{contributor_id} AND article_id = #{article_id}")
    int belong(@Param("contributor_id") String contributor_id, @Param("article_id") String article_id);
}
