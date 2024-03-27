package com.snach.literatureclub.controller;

import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.service.ExpertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "expert", method = RequestMethod.POST)
public class ExpertController {
    private final ExpertService expertService;

    @Autowired
    public ExpertController(ExpertService expertService) {
        this.expertService = expertService;
    }

    @RequestMapping(value = "recommendArticle")
    public boolean recommendArticle(User expert, @RequestParam("article_id") String articleId, @RequestParam("recommend_to") String recommendTo) {
        return expertService.recommendArticle(expert, articleId, recommendTo);
    }
}
