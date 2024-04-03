package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.common.ArticleAuditStatus;
import com.snach.literatureclub.common.ArticlePublishStatus;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ArticleDao {

    // ======================================INSERT==========================================
    /**
     * 插入方法
     *
     * @param article 封装有稿件信息的Article对象
     */
    @Insert("INSERT INTO article(id, title, description, time, audit_status, publish_status, tags, text_by, file_type, raw, mentor, latest_approval_article_url, received_by, author_name, author_org, author_grade, reason, audited_by) " +
            "VALUES(#{a.id}, #{a.title}, #{a.description}, #{a.time}, #{a.auditStatus}, #{a.publishStatus}, #{a.tags}, #{a.textBy}, #{a.fileType}, #{a.raw}, #{a.mentor}, #{a.latestApprovalArticleUrl}, #{a.receivedBy}, #{a.authorName}, #{a.authorOrganization}, #{a.authorGrade}, #{a.reason}, #{a.auditedBy})")
    void insertArticle(@Param("a") Article article);

    @Insert("INSERT INTO article(id, title, description, time, audit_status, publish_status, tags, text_by, mentor, received_by) " +
            "VALUES (#{a.id}, #{a.title},#{a.description}, #{a.time}, #{a.auditStatus}, #{a.publishStatus}, #{a.tags}, #{a.textBy}, #{a.mentor}, #{a.receivedBy})")
    void newInsertArticle(@Param("a") Article article, int ignore);

    // ======================================SELECT==========================================
    @Select("SELECT id, time, text_by as textBy, title, description, audit_status as auditStatus, publish_status as publishStatus, tags, `file_type` as fileType, author_name as authorName, author_org as authorOrganization, author_grade as authorGrade, reason, audited_by as auditedBy " +
            "FROM article " +
            "WHERE ${whereStatement}")
    List<Article> getArticleByPersonalOptions(String whereStatement);

    @Select("SELECT id, title, description, time, audit_status AS auditStatus, publish_status AS publishStatus, tags, text_by as textBy, mentor, file_type as fileType " +
            "FROM article WHERE audit_status = #{status} LIMIT 1 FOR UPDATE")
    Article getArticleByAuditStatus(ArticleAuditStatus status);

    @Select("SELECT id, title, description, time, audit_status AS auditStatus, publish_status AS publishStatus, tags, text_by as textBy, mentor, file_type as fileType " +
            "FROM article WHERE audit_status=#{status} LIMIT 1 FOR UPDATE")
    Article getArticleByPublishStatus(ArticlePublishStatus status);

    @Select("SELECT id, title, description, time, audit_status AS auditStatus, publish_status AS publishStatus, tags, text_by as textBy, mentor, file_type as fileType " +
            "FROM article WHERE audit_status=#{status} AND audited_by=#{auditedBy} LIMIT 1 FOR UPDATE")
    Article getArticleByStatusAndAuditedBy(ArticleAuditStatus status, String auditedBy);
    /**
     * 根据作者id查找其稿件，返回稿件id、标题、描述和时间
     *
     * @param contributor_id 作者id
     * @return
     */
    @Select({"<script>",
            "SELECT ",
            "a.id id,a.title title,a.description description,a.time time,a.audit_status auditStatus, a.publish_status publishStatus, a.tags tags ",
            "FROM article a ",
            "WHERE a.text_by = #{contributor_id} AND a.audit_status in",
            "<foreach collection='audit_status' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            " AND a.publish_status in",
            "<foreach collection='publish_status' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<Article> getArticleByContributorId(@Param("contributor_id") String contributor_id, @Param("audit_status") List<ArticleAuditStatus> auditStatusList, @Param("publish_status") List<ArticlePublishStatus> publishStatusList);

    @Select("SELECT id FROM article where text_by = #{contributor_id}")
    List<String> getAllArticleIdByContributorId(String contributor_id);

    @Select("SELECT COUNT(*) FROM article WHERE text_by = #{contributor_id} AND publish_status = 'POSTED'")
    int getReceivedCount(String contributor_id);

    @Select("SELECT COUNT(*) FROM article WHERE text_by = #{contributor_id} AND publish_status in ('PUBLIC','POSTED')")
    int getPublishedCount(String contributor_id);

    @Select("SELECT COUNT(*) FROM article WHERE text_by = #{contributor_id} AND audit_status = 'ROUGH'")
    int getRoughCount(String contributor_id);

    @Select("SELECT COUNT(*) FROM article WHERE text_by = #{contributor_id} AND audit_status = 'BEING_AUDITED'")
    int getBeingAuditedCount(String contributor_id);

    @Select("SELECT COUNT(*) FROM article WHERE text_by = #{contributor_id} AND audit_status = 'FAIL_AUDITED'")
    int getFailAuditedCount(String contributor_id);

    @Select("SELECT COUNT(*) FROM article WHERE text_by = #{contributor_id} AND ( reason IS NOT NULL AND LENGTH(trim(reason))>0 ) ")
    int getReasonCount(String contributor_id);

    @Select("SELECT COUNT(*) FROM article WHERE text_by = #{contributor_id} AND publish_status = 'FAILED_REVIEW'")
    int getFailedReviewCount(String contributor_id);

    @Select("SELECT COUNT(*) FROM article WHERE text_by = #{contributor_id} AND publish_status = 'POST_RECORD'")
    int getPostRecordCount(String contributor_id);

    /**
     * 根据稿件id获取单个稿件的基础信息
     *
     * @param article_id 稿件id
     * @return id对应的稿件信息的Article对象
     */
    @Select("SELECT a.id id,a.title title,a.description description,a.time time,a.audit_status auditStatus,a.publish_status publishStatus, a.tags tags, a.text_by textBy FROM article a WHERE id = #{id}")
    Article getArticleBasicById(@Param("id") String article_id);

    /**
     * 根据id获取单个稿件的详细信息
     *
     * @param id 稿件id
     * @return id对应的稿件信息的Article对象
     */
    @Select("SELECT id, time, text_by as textBy, title, description, audit_status as auditStatus, publish_status as publishStatus, tags, `file_type` as fileType, author_name as authorName, author_org as authorOrganization, author_grade as authorGrade, reason, audited_by as auditedBy, mentor FROM article WHERE id = #{id}")
    Article getArticleById(@Param("id") String id);

    /**
     * 获取所有的稿件信息
     *
     * @return 所有的稿件信息的Article对象List
     */
    @Select("SELECT id, time, text_by as textBy, title, description, audit_status as auditStatus, publish_status as publishStatus, tags " +
            "FROM article WHERE audit_status = 'AUDITED' AND publish_status in ('POSTED','PUBLIC')")
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
            "id, time, text_by as textBy, title, description, publish_status as publishStatus, tags ",
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
            "id, time, text_by as textBy, title, description, tags, publish_status as publishStatus ",
            "FROM article WHERE audit_status='AUDITED' AND title LIKE '%${keyword}%' AND publish_status in",
            "<foreach collection='items' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<Article> getArticlesByKeyword(String keyword, @Param("items") List<ArticlePublishStatus> statusList);

    @Select("SELECT id, time, text_by as textBy, title, description, audit_status as auditStatus, publish_status as publishStatus, tags" +
            " FROM article WHERE received_by = #{userId} AND audit_status='AUDITED'")
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

    @Select("SELECT id FROM article WHERE audit_status = 'AUDITED' AND publish_status in ('POSTED','PUBLIC')")
    List<String> getAllArticleId();

    // 根据文章id列表查询列表中文章详细信息
    @Select({"<script>",
            "SELECT ",
            " id, time, text_by as textBy, title, description, audit_status as auditStatus, publish_status as publishStatus, tags, `file_type` as fileType, author_name as authorName, author_org as authorOrganization, author_grade as authorGrade, reason, audited_by as auditedBy ",
            "FROM article WHERE id in",
            "<foreach collection='items' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<Article> getArticlesRanking(@Param("items") List<String> idList);

    @Select("SELECT u.identity FROM article a LEFT JOIN user u ON a.received_by=u.id WHERE a.id = #{article_id}")
    User getReceivedBy(@Param("article_id") String articleId);

    // ======================================UPDATE==========================================
    @Insert("UPDATE article SET file_type = #{a.fileType}, raw = #{a.raw}, latest_approval_article_url = #{a.latestApprovalArticleUrl} WHERE id = #{a.id}")
    void updateArticleFile(@Param("a") Article article, boolean ignore);

    @Update("UPDATE article SET latest_approval_article_url = #{latest_approval_article_url} WHERE id = #{article_id}")
    int updateLatestApprovalArticleUrl(@Param("article_id") String articleId, @Param("latest_approval_article_url") String latestApprovalArticleUrl);

    /**
     * 更新稿件保存状态
     *
     * @param status 稿件状态 （1：保存成功 2：待审核 3.已发布 4.未通过 0：保存失败）
     * @param id     文章id
     * @return
     */
    @Update("UPDATE article SET audit_status = #{status} WHERE id = #{id}")
    int updateAuditStatus(@Param("id") String id, @Param("status") ArticleAuditStatus status);

    @Update("UPDATE article SET publish_status = #{status} WHERE id = #{id}")
    int updatePublishStatus(@Param("id") String id, @Param("status") ArticlePublishStatus status);

    @Update("UPDATE article SET audit_status = #{status}, reason = #{reason} WHERE id = #{id}")
    int audit(@Param("id") String id, @Param("status") ArticleAuditStatus status, String reason);

    /**
     * 更新稿件基础信息,包括包括标题和描述
     *
     * @param article 封装有稿件信息的Article对象
     * @return 匹配到的行数（如果想设置返回值是受影响的行数，修改数据库链接配置：增加 useAffectedRows=true 即可）
     */
    @Update("UPDATE article SET title = #{article.title}, description = #{article.description}, time = #{article.time},audit_status = #{article.auditStatus},publish_status = #{article.publishStatus}, tags = #{article.tags} " +
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
    @Update("UPDATE article AS a SET " +
            "a.title = #{a.title}, " +
            "a.description = #{a.description}, " +
            "a.time = #{a.time}, " +
            "a.audit_status = #{a.auditStatus}, " +
            "a.publish_status = #{a.publishStatus}, " +
            "a.tags = #{a.tags}, " +
            "a.text_by = #{a.textBy}, " +
            "a.file_type = #{a.fileType}, " +
            "a.raw = #{a.raw}, " +
            "a.mentor = #{a.mentor}, " +
            "a.latest_approval_article_url = #{a.latestApprovalArticleUrl}, " +
            "a.received_by = #{a.receivedBy}, " +
            "a.author_name = #{a.authorName}, " +
            "a.author_org = #{a.authorOrganization}, " +
            "a.author_grade = #{a.authorGrade}, " +
            "a.reason = #{a.reason}, " +
            "a.audited_by = #{a.auditedBy} " +
            "WHERE a.id = #{a.id}")
    int updateArticleDetail(@Param("a") Article article);

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

}
