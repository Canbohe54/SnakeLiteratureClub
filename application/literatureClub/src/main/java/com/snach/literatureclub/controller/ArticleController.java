package com.snach.literatureclub.controller;


import com.github.pagehelper.PageInfo;
import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.ArticleAuditStatus;
import com.snach.literatureclub.common.ArticlePublishStatus;
import com.snach.literatureclub.common.annotation.ResponseNotIntercept;
import com.snach.literatureclub.service.ArticleService;
import com.snach.literatureclub.service.TagService;
import com.snach.literatureclub.utils.MediaTypeConverter;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "article", method = RequestMethod.POST)
public class ArticleController {
    private final ArticleService articleService;

    private final TagService tagService;

    @Autowired
    public ArticleController(ArticleService articleService, TagService tagService) {
        this.articleService = articleService;
        this.tagService = tagService;
    }

    /**
     * 分页查询返回所有稿件的基础信息
     * 返回格式 { articles: total: #{NUMBER}, list: [#{ARTICLE},...], statusMsg: #{STRING}}
     *
     * @param pageNum  当前页数
     * @param pageSize 页面显示条数
     * @return 所有的稿件信息的Article对象List
     */
    @RequestMapping(value = "allArticles", method = RequestMethod.GET)
    public PageInfo<Article> allArticles(@RequestParam(name = "page_num") int pageNum,
                                         @RequestParam(name = "page_size") int pageSize) {
        PageInfo<Article> pageInfo = articleService.getAllArticles(pageNum, pageSize);
        // 获取每篇文章对应tags
        for(int i=0; i<pageInfo.getList().size(); i++){
            Article article = pageInfo.getList().get(i);
            pageInfo.getList().get(i).setTags(tagService.getPackedTags(article.getId()));
        }
        return pageInfo;
    }

    /**
     * 根据稿件id查找稿件详细信息，包括是否公开、文章状态、文章作者信息和收稿方等
     *
     * @param article_id 稿件id
     * @return 稿件详细信息, 执行状态
     */
    @RequestMapping(value = "articleDetail", method = RequestMethod.GET)
    public Map<String, Object> articleDetail(String article_id) {
        Map<String, Object> response = new HashMap<>();
        Article article = articleService.getArticleById(article_id);
        article.setTags(tagService.getPackedTags(article_id));
        response.put("article", article);
        return response;
    }

    @RequestMapping(value = "getArticles", method = RequestMethod.POST)
    public PageInfo<Article> getArticles(@RequestParam(required = false) List<String> idList,
                                         @RequestParam(required = false) List<String> authorList,
                                         @RequestParam(required = false) List<String> receiverList,
                                         @RequestParam(required = false) List<String> auditorList,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String tags,
                                         @RequestParam(required = false) List<ArticleAuditStatus> auditStatusList,
                                         @RequestParam(required = false) List<ArticlePublishStatus> publishStatusList,
                                         @RequestParam int pageNum,
                                         @RequestParam int pageSize) {
        PageInfo<Article> pageInfo = articleService.getArticles(idList, authorList, receiverList, auditorList, keyword, tags, auditStatusList, publishStatusList, pageNum, pageSize);
        // 获取每篇文章对应tags
        for(int i=0; i<pageInfo.getList().size(); i++){
            Article article = pageInfo.getList().get(i);
            pageInfo.getList().get(i).setTags(tagService.getPackedTags(article.getId()));
        }
        return pageInfo;
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public PageInfo<Article> articleSearch(@RequestParam(value = "keyword") String keyword,
                                           @RequestParam(value = "tag", required = false) String tag,
                                           @RequestParam(name = "page_num") int pageNum,
                                           @RequestParam(name = "page_size") int pageSize,
                                           @RequestParam(name = "status_list") List<ArticlePublishStatus> statusList) {
        PageInfo<Article> pageInfo = articleService.searchArticle(keyword, tag, pageNum, pageSize, statusList);
        // 获取每篇文章对应tags
        for(int i=0; i<pageInfo.getList().size(); i++){
            Article article = pageInfo.getList().get(i);
            pageInfo.getList().get(i).setTags(tagService.getPackedTags(article.getId()));
        }
        return pageInfo;
    }

    /**
     * 通过id获取文章原始文件
     *
     * @param articleId article id
     * @return 原始文件二进制流
     */
    @ResponseNotIntercept
    @RequestMapping(value = "getArticleFileById", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getArticleFileById(@RequestParam(name = "article_id") String articleId) {
        Article article = articleService.getArticleFileById(articleId);
        byte[] fileContent = article.getRaw();
        String type = article.getFileType();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaTypeConverter.getMediaType(type));

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    /**
     * 将文章转换为PDF格式。
     *
     * @param articleId 文章的唯一标识符，用于查找对应的word文档。
     * @return 返回转换后的PDF文件内容作为HTTP响应的一部分。
     * @throws IOException 如果在文件读写过程中发生错误。
     */
    @ResponseNotIntercept
    @RequestMapping(value = "File2Pdf", method = RequestMethod.GET)
    public ResponseEntity<byte[]> word2pdf(@RequestParam(name = "article_id") String articleId) throws IOException {
        // 调用articleService将指定文章ID的文档转换为PDF格式，返回PDF内容。
        byte[] fileContent = articleService.File2Pdf(articleId);
        String type = "application/pdf";

        // 设置HTTP响应头，指定返回的内容类型为PDF。
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaTypeConverter.getMediaType(type));

        // 构建并返回包含PDF文件内容的HTTP响应实体。
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    /**
     * 获取文章的访问权限
     *
     * @param articleId 文章id
     * @param requester 请求者id
     */
    @RequestMapping(value = "getPermissions", method = RequestMethod.GET)
    public boolean getPermissions(String articleId, String requester) {
        return articleService.getPermissions(articleId, requester);
    }

    @RequestMapping(value = "checkLocked", method = RequestMethod.GET)
    public boolean checkLocked(String articleId) {
        return articleService.checkLock(articleId);
    }

    /**
     * 获取文章锁的剩余过期时间
     *
     * @param articleId 文章id
     * @return 剩余过期时间
     */
    @RequestMapping(value = "getArticleLockExpire", method = RequestMethod.GET)
    public long getArticleLockExpire(String articleId) {
        return articleService.getArticleLockExpire(articleId);
    }

    /**
     * 通过id对文章进行敏感词审核
     *
     * @param articleId article id
     * @return 审核结果
     */
    @RequestMapping(value = "SensitiveWordReview", method = RequestMethod.GET)
    public Map<String, Object> sensitiveWordsJudge(String token,
                                                   @RequestParam(name = "article_id") String articleId,
                                                   @RequestParam(defaultValue = "true") boolean useStrict) {
        List<?> sensitiveWordsList = articleService.sensitiveWordsJudge(token, articleId, useStrict);

        Map<String, Object> response = new HashMap<>();
        response.put("sensitiveWords", sensitiveWordsList);
        response.put("num", sensitiveWordsList.size());
        return response;
    }

    @RequestMapping(value = "getLatestApprovalArticleUrl", method = RequestMethod.GET)
    public Map<String, Object> getLatestApprovalArticleUrl(@RequestParam("article_id") String articleId) {
        Map<String, Object> response = new HashMap<>();
        response.put("latest_approval_article_url", articleService.getLatestApprovalArticleById(articleId));
        return response;
    }

    @RequestMapping(value = "getReceivedArticleById", method = RequestMethod.GET)
    public PageInfo<Article> getReceivedArticleById(@RequestParam("auditor_id") String auditorId, @RequestParam("page_num") int pageNum, @RequestParam("page_size") int pageSize) {
        PageInfo<Article> pageInfo = articleService.getReceivedArticleById(auditorId, pageNum, pageSize);
        // 获取每篇文章对应tags
        for(int i=0; i<pageInfo.getList().size(); i++){
            Article article = pageInfo.getList().get(i);
            pageInfo.getList().get(i).setTags(tagService.getPackedTags(article.getId()));
        }
        return pageInfo;
    }

    /**
     * 锁定文章，将锁定者加入该文章可访问列表
     * redis设置锁的过期时间
     *
     * @param articleId 文章id
     * @param expire    过期时间，单位：秒
     * @param lockedBy  锁定者的token
     */
    @RequestMapping(value = "lockArticleById", method = RequestMethod.POST)
    public boolean lockArticleById(@RequestParam("article_id") String articleId, long expire, @RequestParam(name = "locked_by") String lockedBy) {
        return articleService.lockArticleById(articleId, expire, lockedBy);
    }

    /**
     * 解锁文章
     *
     * @param articleId 文章id
     * @param unlockedByToken 解锁者的token
     */
    @RequestMapping(value = "unlockArticleById", method = RequestMethod.POST)
    public boolean unlockArticleById(@RequestParam("article_id") String articleId, @RequestParam(name = "unlocked_by") String unlockedByToken) {
        return articleService.unLockArticleById(articleId, unlockedByToken);
    }

    @RequestMapping(value = "changeArticleStatus", method = RequestMethod.POST)
    public boolean changeArticleStatus(String articleId, ArticleAuditStatus status, String token) {
        return articleService.changeArticleStatus(articleId, status, token);
    }
    @RequestMapping(value = "changeArticlePublishStatus", method = RequestMethod.POST)
    public boolean changeArticlePublishStatus(String articleId, ArticlePublishStatus status, String token) {
        return articleService.changeArticlePublishStatus(articleId, status, token);
    }

    @RequestMapping(value = "changeArticleReceivedBy", method = RequestMethod.POST)
    public boolean changeArticleReceivedBy(String articleId, String receivedBy, String token) {
        return articleService.changeArticleReceivedBy(articleId, receivedBy, token);
    }

    @RequestMapping(value = "getRecievedAndPublishedCount", method = RequestMethod.GET)
    public Map<String, Object> getRecievedAndPublishedCount(String contributorId) {
        return articleService.getRecievedAndPublishedCount(contributorId);
    }

    @RequestMapping(value = "getContributorCenterInformation", method = RequestMethod.GET)
    public Map<String, Object> getContributorCenterInformation(String contributorId) {
        return articleService.getContributorCenterInformation(contributorId);
    }
}
