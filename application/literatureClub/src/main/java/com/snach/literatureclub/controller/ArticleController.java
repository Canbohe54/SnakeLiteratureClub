package com.snach.literatureclub.controller;


import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
     * 返回所有稿件的详细信息
     * 返回格式 { articles: [#{ARTICLE},...], statusMsg: #{STRING}}
     *
     * @return 所有的稿件信息的Article对象List
     */
    @RequestMapping(value = "allArticles", method = RequestMethod.GET)
    public Map<String, Object> allArticles(@RequestParam(name = "token", required = false) String token) {
        return articleService.getAllArticles();
    }

    /**
     * 根据稿件id查找稿件详细信息，包括标题、描述、修改时间和内容，用于编辑界面显示
     * 返回格式{ article: #{ARTICLE}, statusMsg: #{STRING}}
     *
     * @param article_id 稿件id
     * @return 稿件详细信息, 执行状态
     */
    @RequestMapping(value = "articleDetail", method = RequestMethod.GET)
    public Map<String, Object> articleDetail(@RequestParam("article_id") String article_id) {
        return articleService.getArticleById(article_id);
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public Map<String, Object> articleSearch(@RequestParam(value = "keyword") String keyword,
                                             @RequestParam(value = "tag", required = false) String tag) {
        return articleService.searchArticle(keyword, tag);
    }
}
