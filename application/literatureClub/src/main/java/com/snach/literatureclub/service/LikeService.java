package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.like.ArticleLike;
import com.snach.literatureclub.bean.like.LikesAndViewCount;
import com.snach.literatureclub.common.CONSTANT;
import com.snach.literatureclub.common.exception.InvalidTokenException;
import com.snach.literatureclub.dao.ArticleDao;
import com.snach.literatureclub.dao.ArticleLikeDao;
import com.snach.literatureclub.dao.ArticleLikeAndViewCountDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.snach.literatureclub.utils.TokenTools.tokenVerify;

public interface LikeService {
    Map<String, Object> like(String token, String articleId, String userId);
    Map<String, Object> cancelLike(String token, String articleId, String userId);
    Map<String, Object> getCurrentLikeStatus(String articleId, String userId);
    Map<String, Object> getCurrentLikeCount(String articleId);
    Map<String, Object> getAllLikeCount();
}

@Transactional(rollbackFor = Exception.class)
@Service
@Mapper
class LikeServiceImpl implements LikeService {

    @Autowired
    RedisService redisService;

    @Autowired
    ArticleLikeDao articleLikeDao;

    @Autowired
    ArticleLikeAndViewCountDao articleLikeAndViewCountDao;

    @Autowired
    DBService dbService;

    @Autowired
    ArticleDao articleDao;

    @Override
    public Map<String, Object> like(String token, String articleId, String userId) {
        // 检测token是否合法
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        Map<String, Object> res = new HashMap<>();
        // 先判断点赞数信息是否在redis中，如果不在要先加载到redis中
        if(redisService.getLikeCountStatus(articleId) == CONSTANT.LikedStatusEum.NOT_EXIST.getCode()){
            LikesAndViewCount likesAndViewCount = dbService.getLikeAndViewCountByArticleIdFromDB(articleId);
            // 如果数据库里面有信息
            if(likesAndViewCount!=null){
                redisService.saveLikedCount2Redis(articleId, likesAndViewCount.getLike_count());
            }
            else{
                redisService.saveLikedCount2Redis(articleId, 0);
            }
        }
        // 判断点赞信息是否在redis中
        // 如果有
        if(redisService.getLikeStatus(articleId, userId)!= CONSTANT.LikedStatusEum.NOT_EXIST.getCode()){
            // 如果是点赞，则更改点赞状态为取消点赞，点赞数-1
            if(redisService.getLikeStatus(articleId, userId)==CONSTANT.LikedStatusEum.LIKE.getCode()){
                redisService.saveUnLiked2Redis(articleId, userId);
                redisService.in_decrementLikedCount(articleId, -1);
                res.put("currentStatus", "unlike");
            }
            // 如果是未点赞，则更改点赞状态为点赞，点赞数+1
            else{
                redisService.saveLiked2Redis(articleId, userId);
                redisService.in_decrementLikedCount(articleId, 1);
                res.put("currentStatus", "like");
            }
        }
        // 如果没有信息，则需要从数据库获取，再存入redis
        else{
            ArticleLike articleLike = dbService.getByArticleIdAndLikeUserId(articleId, userId);
            // 如果数据库中有信息
            if(articleLike!=null){
                if(articleLike.getStatus()==CONSTANT.LikedStatusEum.LIKE.getCode()){
                    //如果是点赞，则保存点赞状态为取消点赞，点赞数-1
                    redisService.saveUnLiked2Redis(articleId, userId);
                    redisService.in_decrementLikedCount(articleId, -1);
                    res.put("currentStatus", "unlike");
                }
                else{
                    // 如果是未点赞，则保存点赞状态为点赞，点赞数+1
                    redisService.saveLiked2Redis(articleId, userId);
                    redisService.in_decrementLikedCount(articleId, 1);
                    res.put("currentStatus", "like");
                }
            }
            // 如果数据库中没有信息,表明是第一次点赞，直接保存在redis,点赞数+1
            else{
                redisService.saveLiked2Redis(articleId, userId);
                redisService.in_decrementLikedCount(articleId, 1);
                res.put("currentStatus", "like");
            }
        }
        return res;
    }

    @Override
    public Map<String, Object> cancelLike(String token, String articleId, String userId) {
        return null;
    }

    @Override
    public Map<String, Object> getCurrentLikeStatus(String articleId, String userId) {
        Map<String, Object> res = new HashMap<>();
        // 如果点赞信息是在redis中
        if(redisService.getLikeStatus(articleId, userId)!=CONSTANT.LikedStatusEum.NOT_EXIST.getCode()){
            if(redisService.getLikeStatus(articleId, userId)==CONSTANT.LikedStatusEum.LIKE.getCode()){
                res.put("currentStatus", "like");
            }
            else{
                res.put("currentStatus", "unlike");
            }
        }
        // 否则在数据库中查找点赞状态
        else{
            ArticleLike articleLike = dbService.getByArticleIdAndLikeUserId(articleId, userId);
            if(articleLike!=null){
                // 将数据库中的点赞信息存入redis
                redisService.saveArticleLike2Redis(articleLike);
                if(articleLike.getStatus()==CONSTANT.LikedStatusEum.LIKE.getCode()) {
                    res.put("currentStatus", "like");
                }
                else{
                    res.put("currentStatus", "unlike");
                }
            }
            // 如果数据库中也没有信息，说明还未进行过点赞
            else{
                redisService.saveUnLiked2Redis(articleId, userId);
                res.put("currentStatus", "unlike");
            }
        }
        return res;
    }

    @Override
    public Map<String, Object> getCurrentLikeCount(String articleId) {
//        Map<String, Object> res = new HashMap<>();
//        int count = redisService.getLikedCountByArticleIdFromRedis(articleId) + dbService.getLikeCountByArticleIdFromDB(articleId);
//        res.put("currentLikeCount", count);
//        return res;
        Map<String, Object> res = new HashMap<>();
        int count = redisService.getLikeCountStatus(articleId);
        // 如果redis中有点赞量信息，直接在redis获取
        if(count!= CONSTANT.LikedStatusEum.NOT_EXIST.getCode()){
            res.put("currentLikeCount", count);
        }
        // 否则从数据库获取，并加载到redis
        else{
            LikesAndViewCount likesAndViewCount = dbService.getLikeAndViewCountByArticleIdFromDB(articleId);
            // 如果数据库里面有信息
            if(likesAndViewCount!=null){
                redisService.saveLikedCount2Redis(articleId, likesAndViewCount.getLike_count());
                res.put("currentLikeCount", likesAndViewCount.getLike_count());
            }
            else{
                redisService.saveLikedCount2Redis(articleId, 0);
                res.put("currentLikeCount", "0");
            }
        }
        return res;
    }

    @Override
    public Map<String, Object> getAllLikeCount() {
        Map<String, Object> res = new HashMap<>();
        Map<String, Integer> articleLikeCountMap = new HashMap<>();
        // 先获取文章id列表
        List<String> articleIdList = articleDao.getAllArticleId();
        // 遍历文章id列表，获取每个文章的点赞数
        for(String articleId: articleIdList){
            // 获取文章点赞数
            int count = (int) getCurrentLikeCount(articleId).get("currentLikeCount");
            articleLikeCountMap.put(articleId, count);
        }
        res.put("articleLikeCountMap", articleLikeCountMap);
        // 根据articleLikeCountMap中的value从大到小对key进行排序，存到列表中
        List<String> articleRanking = articleLikeCountMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        res.put("articleRanking", articleRanking);
        return res;
    }
}
