package com.snach.literatureclub.controller;

import com.snach.literatureclub.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "common", method = RequestMethod.GET)
public class CommonController {
    private final CommonService commonService;

    @Autowired
    public CommonController(CommonService commonService) {
        this.commonService = commonService;
    }

    @RequestMapping(value = "articleTags", method = RequestMethod.GET)
    public Map<String, Object> loadArticleTags() {
        Map<String, Object> response = new HashMap<>();
        response.put("tags", commonService.loadArticleTags());
        return response;
    }
}
