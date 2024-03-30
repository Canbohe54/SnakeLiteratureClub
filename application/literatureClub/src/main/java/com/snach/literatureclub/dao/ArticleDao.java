package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.ArticleStatus;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ArticleDao {

    // ======================================INSERT==========================================

    /**
     * 插入作者与稿件关系
     *
     * @param contributor_id 作者id
     * @param article_id     稿件id
     */
    @Insert("INSERT INTO contributor_article_list(contributor_id,article_id) VALUES(#{c_id},#{a_id})")
    void insertRelation(@Param("c_id") String contributor_id, @Param("a_id") String article_id);

    /**
     * 插入方法
     *
     * @param article 封装有稿件信息的Article对象
     */
    @Insert("INSERT INTO article(id, title, description, text,`time`, status, tags, text_by, raw, mentor, file_type, received_by, audited_by) " +
            "VALUES(#{a.id}, #{a.title},#{a.description}, #{a.text}, #{a.time},#{a.status},#{a.tags},#{a.textBy},#{a.raw},#{a.mentor},#{a.fileType},#{a.receivedBy}, #{a.auditedBy})")
    void insertArticle(@Param("a") Article article);

    // ======================================SELECT==========================================

    @Select("SELECT id, title,description,text,`time`,status, tags, text_by as textBy, mentor, file_type as fileType" +
            " FROM article WHERE status=#{status} LIMIT 1 FOR UPDATE")
    Article getArticleByStatus(ArticleStatus status);

    @Select("SELECT id, title,description,text,`time`,status, tags, text_by as textBy, mentor, file_type as fileType" +
            " FROM article WHERE status=#{status} AND audited_by=#{auditedBy} LIMIT 1 FOR UPDATE")
    Article getArticleByStatusAndAuditedBy(ArticleStatus status, String auditedBy);
    /**
     * 根据作者id查找其稿件，返回稿件id、标题、描述和时间
     *
     * @param contributor_id 作者id
     * @return
     */
    @Select({"<script>",
            "SELECT ",
            "a.id id,a.title title,a.description description,a.time time,a.status status, a.tags tags ",
            "FROM article a left join contributor_article_list c on a.id = c.article_id ",
            "WHERE c.contributor_id = #{contributor_id} AND a.status in",
            "<foreach collection='items' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<Article> getArticleByContributorId(@Param("contributor_id") String contributor_id, @Param("items") List<ArticleStatus> statusList);
    /**
    * 获取用户文章数量
    * */
    @Select({"<script>",
            "SELECT ",
            "COUNT(*)",
            "FROM article a left join contributor_article_list c on a.id = c.article_id ",
            "WHERE c.contributor_id = #{contributor_id} AND a.status = 3",
            "</script>"
    })
    int getArticleNumByContributorId(@Param("contributor_id") String contributor_id);
    /**
     * 根据稿件id获取单个稿件的基础信息
     *
     * @param article_id 稿件id
     * @return id对应的稿件信息的Article对象
     */
    @Select("SELECT a.id id,a.title title,a.description description,a.time time,a.status status, a.tags tags, a.text_by textBy FROM article a WHERE id = #{id}")
    Article getArticleBasicById(@Param("id") String article_id);

    /**
     * 根据id获取单个稿件的详细信息
     *
     * @param id 稿件id
     * @return id对应的稿件信息的Article对象
     */
    @Select("SELECT id, text, time, text_by as textBy, title, description, status, tags, `file_type` as fileType, author_name as authorName, author_org as authorOrganization, author_grade as authorGrade, reason, audited_by as auditedBy FROM article WHERE id = #{id}")
    Article getArticleById(@Param("id") String id);

    /**
     * 获取所有的稿件信息
     *
     * @return 所有的稿件信息的Article对象List
     */
    @Select("SELECT id, time, text_by as textBy, title, description, status, tags FROM article WHERE status = 'PUBLISHED'")
    List<Article> getAllArticles();

    /**
     * 根据名称关键字和标签获取稿件
     *
     * @param keyword 关键字
     * @param tag     标签
     * @return 所有符合条件的稿件
     */
    @Select({"<script>",
            "SELECT ",
            "id, text, time, text_by as textBy, title, description, status, tags ",
            "FROM article WHERE title LIKE '%${keyword}%' AND tags LIKE '%\"tags\":%\"${tag}\"%]%' AND status in",
            "<foreach collection='items' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<Article> getArticlesByKeywordAndTag(String keyword, String tag);

    /**
     * 根据名称关键字获取稿件
     *
     * @param keyword 关键字
     * @return 所有符合条件的稿件
     */
    @Select({"<script>",
            "SELECT ",
            "id, time, text_by as textBy, title, description, status, tags ",
            "FROM article WHERE title LIKE '%${keyword}%' AND status in",
            "<foreach collection='items' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<Article> getArticlesByKeyword(String keyword, @Param("items") List<ArticleStatus> statusList);

    @Select("SELECT id, time, text_by as textBy, title, description, status, tags" +
            " FROM article WHERE received_by = #{userId}")
    List<Article> getArticleByReceivedBy(String userId);

    /**
     * 判断稿件是否属于该作者
     *
     * @param contributor_id 作者id
     * @param article_id     稿件id
     * @return 匹配到的行数
     */
    @Select("Select COUNT(*) FROM contributor_article_list WHERE contributor_id = #{contributor_id} AND article_id = #{article_id}")
    int belong(@Param("contributor_id") String contributor_id, @Param("article_id") String article_id);

    @Select("SELECT raw, `file_type` AS fileType FROM article WHERE id=#{article_id}")
    Article getArticleFileById(@Param("article_id") String articleId);

    @Select("SELECT latest_approval_article_url FROM article WHERE id=#{article_id}")
    String getLatestApprovalArticleUrlById(@Param("article_id") String articleId);

    // ======================================UPDATE==========================================

    @Update("UPDATE article SET latest_approval_article_url = #{latest_approval_article_url} WHERE id = #{article_id}")
    int updateLatestApprovalArticleUrl(@Param("article_id") String articleId, @Param("latest_approval_article_url") String latestApprovalArticleUrl);

    /**
     * 更新稿件保存状态
     *
     * @param status 稿件状态 （1：保存成功 2：待审核 3.已发布 4.未通过 0：保存失败）
     * @param id     文章id
     * @return
     */
    @Update("UPDATE article SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("status") ArticleStatus status, @Param("id") String id);

    @Update("UPDATE article SET status = #{status}, reason = #{reason} WHERE id = #{id}")
    int audit(@Param("id") String id, @Param("status") ArticleStatus status, String reason);

    /**
     * 更新稿件基础信息,包括包括标题和描述
     *
     * @param article 封装有稿件信息的Article对象
     * @return 匹配到的行数（如果想设置返回值是受影响的行数，修改数据库链接配置：增加 useAffectedRows=true 即可）
     */
    @Update("UPDATE article SET title = #{article.title}, description = #{article.description}, time = #{article.time},status = #{article.status}, tags = #{article.tags} " +
            "WHERE id = #{article.id}")
    int updateArticleInfo(@Param("article") Article article);

    /**
     * 更新稿件接收者
     *
     * @param articleId  稿件id
     * @param receivedBy 接收者id
     * @return 匹配到的行数（如果想设置返回值是受影响的行数，修改数据库链接配置：增加 useAffectedRows=true 即可）
     */
    @Update("UPDATE article SET received_by = #{receivedBy} WHERE id = #{articleId}")
    int updateReceivedBy(String articleId, String receivedBy);

    /**
     * 更新稿件详细信息,包括包括标题、描述和内容
     *
     * @param article 封装有稿件信息的Article对象
     * @return 匹配到的行数（如果想设置返回值是受影响的行数，修改数据库链接配置：增加 useAffectedRows=true 即可）
     */
    @Update("UPDATE article SET " +
            "title = #{article.title}, " +
            "description = #{article.description}, " +
            "text = #{article.text}, " +
            "`time` = #{article.time}, " +
            "status = #{article.status}, " +
            "tags = #{article.tags}, " +
            "raw = #{article.raw}, " +
            "mentor = #{article.mentor}, " +
            "file_type = #{article.fileType}, " +
            "received_by = #{article.receivedBy} " +
            "WHERE id = #{article.id}")
    int updateArticleDetail(@Param("article") Article article);

    @Update("UPDATE article SET audited_by=#{auditedBy} WHERE id=#{articleId}")
    int updateAuditedBy(String articleId, String auditedBy);

    // ======================================DELETE==========================================
    /**
     * 根据稿件id进行删除
     *
     * @param id 稿件id
     */
    @Delete("DELETE FROM article WHERE id = #{id}")
    void deleteArticleById(@Param("id") String id);

    @Select("SELECT id FROM article")
    List<String> getAllArticleId();
}
