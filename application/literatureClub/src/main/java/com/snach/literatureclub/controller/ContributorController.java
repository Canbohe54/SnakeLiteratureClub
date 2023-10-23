package com.snach.literatureclub.controller;

import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "contributor", method = RequestMethod.POST)
public class ContributorController {
    @Autowired
    private final ArticleService articleService;

    public ContributorController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 用户对稿件进行保存（1）或上传（2）
     * 初次保存或上传传入id为null,
     * 返回稿件基本信息（标题、描述、时间和id）和各事件执行状态
     * 返回格式{article_id: #{String}, title: #{String}, description: #{String}, time:#{Date}, fileStatue: #{INTEGER}, statusMsg: #{STRING} }
     *
     * @param token   用于验证是否过期以及获取作者id
     * @param article 稿件信息，id初次创建为null
     * @return 稿件基本信息（标题、描述、时间和id）,保存状态（1：保存成功 2：待审核 0：保存失败）,执行状态
     * @see Article
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public Map<String, Object> save(@RequestParam("token") String token, Article article) {
        return articleService.addArticle(token, article);
    }

    /**
     * 用户更改稿件基础信息，包括标题和描述，根据id进行更新，同时更新修改时间
     * 返回格式{article_id: #{String}, title: #{String}, description: #{String}, time:#{Date}, fileStatue: #{INTEGER}, statusMsg: #{STRING} }
     *
     * @param token       用于验证是否过期以及获取作者id
     * @param id          稿件id
     * @param title       稿件标题
     * @param description 稿件描述
     * @param attr        稿件多值属性，如标签
     * @return 保存状态（1：保存成功 2：待审核 0：保存失败）,执行状态
     */
    @RequestMapping(value = "changeBasicInfo", method = RequestMethod.POST)
    public Map<String, Object> changeBasicInfo(@RequestParam("token") String token,
                                               @RequestParam("article_id") String id,
                                               @RequestParam("title") String title,
                                               @RequestParam("description") String description,
                                               @RequestParam("attr") String attr) {
        return articleService.updateArticle(token, id, title, description, 3, attr);
    }

    /**
     * 根据id删除稿件
     * 返回格式{ statusMsg: #{STRING} }
     *
     * @param token 用于验证是否过期以及获取作者id
     * @param id    稿件id
     * @return 执行状态
     */
    @RequestMapping(value = "delArticle")
    public Map<String, Object> deleteArticle(@RequestParam("token") String token, @RequestParam("article_id") String id) {
        return articleService.deleteArticle(token, id);
    }

    /**
     * 根据作者id查找其稿件基础信息，包括标题、修改时间和描述，用于创作者界面显示
     *
     * @param contributor_id 作者id
     * @return 作者稿件列表, 执行状态 返回格式{ articles: [#{ARTICLE},...], statusMsg: #{STRING}}
     */
    @RequestMapping(value = "contributorArticles", method = RequestMethod.GET)
    public Map<String, Object> contributorArticles(@RequestParam("contributor_id") String contributor_id) {
        return articleService.getContributorArticles(contributor_id);
    }

}
