package com.snach.literatureclub.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snach.literatureclub.dao.NewArticleDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TagService {
    String getPackedTags(String article_id);
}

@Transactional(rollbackFor = Exception.class)
@Service
@Mapper
class TagServiceImpl implements TagService {
    private final NewArticleDao newArticleDao;

    @Autowired
    public TagServiceImpl(NewArticleDao newArticleDao) {
        this.newArticleDao = newArticleDao;
    }

    @Override
    public String getPackedTags(String articleId) {
        Gson gson = new GsonBuilder().create();
        List<String> tagList = newArticleDao.getTagsByArticleId(articleId);
        Map<String, List<String>> tagMap = new HashMap<>();
        for (String articleTag : tagList) {
            String[] tag = articleTag.split(":");
            if (tagMap.containsKey(tag[0])) {
                tagMap.get(tag[0]).add(tag[1]);
            } else {
                List<String> tagList1 = new ArrayList<>();
                tagList1.add(tag[1]);
                tagMap.put(tag[0], tagList1);
            }
        }
        return gson.toJson(tagMap);
    }
}