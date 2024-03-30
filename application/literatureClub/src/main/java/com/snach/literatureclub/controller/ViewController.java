package com.snach.literatureclub.controller;


import com.snach.literatureclub.service.ViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "view", method = RequestMethod.POST)
public class ViewController {

    @Autowired
    private ViewService viewService;
    @RequestMapping(value = "addViewCount", method = RequestMethod.POST)
    public Map<String, Object> addViewCount(String articleId) {
        return viewService.addViewCount(articleId);
    }

    @RequestMapping(value = "getViewCount", method = RequestMethod.GET)
    public Map<String, Object> getViewCount(String articleId) {
        return viewService.getViewCount(articleId);
    }
}
