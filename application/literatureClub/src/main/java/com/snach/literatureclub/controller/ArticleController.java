package com.snach.literatureclub.controller;


import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.ArticleStatus;
import com.snach.literatureclub.common.annotation.ResponseNotIntercept;
import com.snach.literatureclub.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * @param token 非必要参数
     * @param pageNum 当前页数
     * @param pageSize 页面显示条数
     * @return 所有的稿件信息的Article对象List
     */
    @ResponseNotIntercept
    @RequestMapping(value = "allArticles", method = RequestMethod.GET)
    public Map<String, Object> allArticles(@RequestParam(name = "token", required = false) String token,
                                           @RequestParam(name = "page_num")int pageNum,
                                           @RequestParam(name = "page_size")int pageSize) {

        return articleService.getAllArticles(pageNum,pageSize);
    }

    /**
     * 根据稿件id查找稿件详细信息，包括标题、描述、修改时间和内容，用于编辑界面显示
     * 返回格式{ article: #{ARTICLE}, statusMsg: #{STRING}}
     *
     * @param article_id 稿件id
     * @return 稿件详细信息, 执行状态
     */
    @ResponseNotIntercept
    @RequestMapping(value = "articleDetail", method = RequestMethod.GET)
    public Map<String, Object> articleDetail(@RequestParam("article_id") String article_id) {
        return articleService.getArticleById(article_id);
    }
    @ResponseNotIntercept
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public Map<String, Object> articleSearch(@RequestParam(value = "keyword") String keyword,
                                             @RequestParam(value = "tag", required = false) String tag,
                                             @RequestParam(name = "page_num")int pageNum,
                                             @RequestParam(name = "page_size")int pageSize,
                                             @RequestParam(name = "status_list")List<ArticleStatus> statusList) {
        return articleService.searchArticle(keyword, tag,pageNum,pageSize,statusList);
    }
}
