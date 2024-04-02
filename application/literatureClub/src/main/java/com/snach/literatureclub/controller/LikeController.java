package com.snach.literatureclub.controller;


import com.snach.literatureclub.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "like", method = RequestMethod.POST)
public class LikeController {
    @Autowired
    LikeService likeService;

    @RequestMapping(value = "like", method = RequestMethod.POST)
    public Map<String, Object> like(String token, String articleId, String userId) {
        return likeService.like(token, articleId, userId);
    }

    @RequestMapping(value = "getLikeCountByArticleID", method = RequestMethod.GET)
    public Map<String, Object> getCurrentLikeCount(String articleId) {
        return likeService.getCurrentLikeCount(articleId);
    }

    @RequestMapping(value = "getLikeStatus", method = RequestMethod.GET)
    public Map<String, Object> getCurrentLikeStatus(String articleId, String userId) {
        return likeService.getCurrentLikeStatus(articleId, userId);
    }

    @RequestMapping(value = "getAllLikeAndViewCount", method = RequestMethod.GET)
    public Map<String, Object> getAllLikeAndViewCount() {
        return likeService.getAllLikeAndViewCount();
    }
}
