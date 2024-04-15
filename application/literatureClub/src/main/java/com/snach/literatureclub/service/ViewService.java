package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.like.LikesAndViewCount;
import com.snach.literatureclub.common.CONSTANT;
import com.snach.literatureclub.dao.NewArticleDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snach.literatureclub.utils.TokenTools.tokenVerify;

public interface ViewService {
    Map<String, Object> addViewCount(String articleId);
    Map<String, Object> getViewCount(String articleId);
    Map<String, Object> getAllViewCountByContributorId(String contributorId);
}

@Transactional(rollbackFor = Exception.class)
@Service
@Mapper
class ViewServiceImpl implements ViewService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private DBService dbService;
    @Autowired
    private NewArticleDao newArticleDao;
    public Map<String, Object> addViewCount(String articleId) {
        Map<String, Object> res = new HashMap<>();
        // 判断浏览量信息是否在redis中
        // 如果有
        if(redisService.getViewStatus(articleId)!= CONSTANT.LikedStatusEum.NOT_EXIST.getCode()){
            // 如果redis中有信息，则浏览量+1
            int currentViewCount = redisService.in_decrementViewCount(articleId, 1);
            res.put("currentViewCount", currentViewCount);
        }
        // 如果没有信息，则需要从数据库获取，再存入redis
        else{
            LikesAndViewCount likesAndViewCount = dbService.getLikeAndViewCountByArticleIdFromDB(articleId);
            // 如果数据库中有信息
            if(likesAndViewCount!=null){
                // 保存浏览量信息到redis，浏览量+1
                redisService.saveViews2Redis(articleId, likesAndViewCount.getView_count()+1);
                res.put("currentViewCount", likesAndViewCount.getView_count()+1);
            }
            // 如果数据库中没有信息,表明是第一次浏览，直接保存在redis
            else{
                redisService.saveViews2Redis(articleId, 1);
                res.put("currentViewCount", "1");
            }
        }
        return res;
    }

    public Map<String, Object> getViewCount(String articleId) {
        Map<String, Object> res = new HashMap<>();
        int count = redisService.getViewStatus(articleId);
        // 如果redis中有浏览量信息，直接在reids获取
        if(count!= CONSTANT.LikedStatusEum.NOT_EXIST.getCode()){
            res.put("currentViewCount", count);
        }
        // 否则从数据库获取，并加载到redis
        else{
            LikesAndViewCount likesAndViewCount = dbService.getLikeAndViewCountByArticleIdFromDB(articleId);
            // 如果数据库里面有信息
            if(likesAndViewCount!=null){
                redisService.saveViews2Redis(articleId, likesAndViewCount.getView_count());
                res.put("currentViewCount", likesAndViewCount.getView_count());
            }
            else{
                redisService.saveViews2Redis(articleId, 0);
                res.put("currentViewCount", 0);
            }
        }
        return res;
    }

    @Override
    public Map<String, Object> getAllViewCountByContributorId(String contributorId) {
        List<String> articleIdList = newArticleDao.getAllArticleIdByContributorId(contributorId);
        int count = 0;
        for(String articleId: articleIdList){
            count += (Integer) getViewCount(articleId).get("currentViewCount");
        }
        Map<String, Object> res = new HashMap<>();
        res.put("allViewCount", count);
        return res;
    }
}
