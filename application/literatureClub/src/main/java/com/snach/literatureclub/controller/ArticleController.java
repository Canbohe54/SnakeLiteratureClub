package com.snach.literatureclub.controller;


import com.github.pagehelper.PageInfo;
import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.ArticleStatus;
import com.snach.literatureclub.common.annotation.ResponseNotIntercept;
import com.snach.literatureclub.service.ArticleService;
import com.snach.literatureclub.utils.MediaTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "article", method = RequestMethod.POST)
public class ArticleController {
    @Autowired
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 分页查询返回所有稿件的基础信息
     * 返回格式 { articles: total: #{NUMBER}, list: [#{ARTICLE},...], statusMsg: #{STRING}}
     *
     * @param token    非必要参数
     * @param pageNum  当前页数
     * @param pageSize 页面显示条数
     * @return 所有的稿件信息的Article对象List
     */
    @ResponseNotIntercept
    @RequestMapping(value = "allArticles", method = RequestMethod.GET)
    public Map<String, Object> allArticles(@RequestParam(name = "token", required = false) String token,
                                           @RequestParam(name = "page_num") int pageNum,
                                           @RequestParam(name = "page_size") int pageSize) {

        return articleService.getAllArticles(pageNum, pageSize);
    }

    /**
     * 根据稿件id查找稿件详细信息，包括标题、描述、修改时间和内容，用于编辑界面显示
     * 返回格式{ article: #{ARTICLE}, statusMsg: #{STRING}}
     *
     * @param article_id 稿件id
     * @return 稿件详细信息, 执行状态
     */
    @RequestMapping(value = "articleDetail", method = RequestMethod.GET)
    public Map<String, Object> articleDetail(String token, String article_id) {
        Map<String, Object> response = new HashMap<>();
        response.put("article", articleService.getArticleById(token, article_id));
        return response;
    }

    @ResponseNotIntercept
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public Map<String, Object> articleSearch(@RequestParam(value = "keyword") String keyword,
                                             @RequestParam(value = "tag", required = false) String tag,
                                             @RequestParam(name = "page_num") int pageNum,
                                             @RequestParam(name = "page_size") int pageSize,
                                             @RequestParam(name = "status_list") List<ArticleStatus> statusList) {
        return articleService.searchArticle(keyword, tag, pageNum, pageSize, statusList);
    }

    /**
     * 通过id获取文章原始文件
     *
     * @param articleId
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

    @RequestMapping(value = "unlockArticleById", method = RequestMethod.POST)
    public boolean unlockArticleById(@RequestParam("article_id") String articleId, @RequestParam(name = "unlocked_by") String unlockedBy) {
        return articleService.unLockArticleById(articleId, unlockedBy);
    }

    /**
     * 获取文章的访问权限
     *
     * @param articleId 文章id
     * @param requester 请求者id
     */
    @RequestMapping(value = "getPermissions", method = RequestMethod.GET)
    public boolean getPermissions(@RequestParam("article_id") String articleId, @RequestParam(name = "requester") String requester) {
        return articleService.getPermissions(articleId, requester);
    }

    /**
     * 通过id对文章进行敏感词审核
     *
     * @param articleId
     * @return 审核结果
     */
    @ResponseNotIntercept
    @RequestMapping(value = "SensitiveWordReview", method = RequestMethod.GET)
    public Map<String, Object> SensitiveWordReview(String token,
                                                   @RequestParam(name = "article_id") String articleId,
                                                   @RequestParam(defaultValue = "false") boolean useStrict) {
        return articleService.SensitiveWordReview(token, articleId, useStrict);
    }

    @RequestMapping(value = "getLatestApprovalArticleUrl", method = RequestMethod.GET)
    public Map<String, Object> getLatestApprovalArticleUrl(@RequestParam("article_id") String articleId) {
        Map<String, Object> response = new HashMap<>();
        response.put("latest_approval_article_url", articleService.getLatestApprovalArticleById(articleId));
        return response;
    }

    @RequestMapping(value = "getReceivedArticleById", method = RequestMethod.GET)
    public PageInfo getReceivedArticleById(@RequestParam("auditor_id") String auditorId, @RequestParam("page_num") int pageNum, @RequestParam("page_size") int pageSize) {
        return articleService.getReceivedArticleById(auditorId, pageNum, pageSize);
    }


}
