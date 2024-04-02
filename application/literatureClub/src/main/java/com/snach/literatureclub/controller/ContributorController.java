package com.snach.literatureclub.controller;

import com.github.pagehelper.PageInfo;
import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.ArticleAuditStatus;
import com.snach.literatureclub.common.ArticlePublishStatus;
import com.snach.literatureclub.service.ArticleService;
import com.snach.literatureclub.service.ContributorService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "contributor", method = RequestMethod.POST)
public class ContributorController {
    private final ArticleService articleService;

    private final ContributorService contributorService;

    @Autowired
    public ContributorController(ArticleService articleService, ContributorService contributorService) {
        this.articleService = articleService;
        this.contributorService = contributorService;
    }

    /**
     * 用户对稿件进行保存（1）或上传（2）
     * 初次保存或上传传入id为null,
     * 返回稿件基本信息（标题、描述、时间和id）和各事件执行状态
     *
     * @param token   用于验证是否过期以及获取作者id
     * @param article 稿件信息，id初次创建为null
     * @return 稿件基本信息（标题、描述、时间和id）,保存状态（1：保存成功 2：待审核 0：保存失败）,执行状态
     * @see Article
     */
    @RequestMapping(value = "contribute", method = RequestMethod.POST)
    public Map<String, Object> contribute(String token, Article article, @RequestParam("raw_file") @Nullable MultipartFile raw) {
        return contributorService.contribute(token, article, raw).packBasicInfo();
    }

    /**
     * 根据id删除稿件
     *
     * @param token 用于验证是否过期以及获取作者id
     * @param id    稿件id
     * @return 执行状态
     */
    @RequestMapping(value = "delArticle")
    public boolean deleteArticle(@RequestParam("token") String token, @RequestParam("article_id") String id) {
        return articleService.deleteArticleById(token, id);
    }

    /**
     * 根据作者id查找其稿件基础信息，包括标题、修改时间和描述，用于创作者界面显示
     *
     * @param contributor_id 作者id
     * @return 作者稿件列表, 执行状态 返回格式{ articles: [#{ARTICLE},...], statusMsg: #{STRING}}
     */
    @RequestMapping(value = "contributorArticles", method = RequestMethod.GET)
    public PageInfo<Article> contributorArticles(@RequestParam("contributor_id") String contributor_id,
                                                 @RequestParam(name = "page_num") int pageNum,
                                                 @RequestParam(name = "page_size") int pageSize,
                                                 @RequestParam(name = "audit_status_list") List<ArticleAuditStatus> auditStatusList,
                                                 @RequestParam(name = "publish_status_list") List<ArticlePublishStatus> publishStatusList) {
        return articleService.getContributorArticles(contributor_id, pageNum, pageSize, auditStatusList, publishStatusList);
    }

}
