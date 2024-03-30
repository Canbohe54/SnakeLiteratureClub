package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.like.ArticleLike;
import com.snach.literatureclub.bean.like.LikesAndViewCount;
import com.snach.literatureclub.common.CONSTANT;
import com.snach.literatureclub.utils.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RedisService {
    Integer getLikeStatus(String articleId, String userId);
    Integer getViewStatus(String articleId);
    Integer getLikeCountStatus(String articleId);
    void saveLiked2Redis(String infoId, String likeUserId);
    void saveUnLiked2Redis(String infoId, String likeUserId);
    void saveArticleLike2Redis(ArticleLike articleLike);
    void saveLikedCount2Redis(String articleId, int likeCount);
    void saveViews2Redis(String articleId, int viewCount);
    void in_decrementLikedCount(String articleId, Integer delta);
    int in_decrementViewCount(String articleId, Integer delta);
    int getLikedCountByArticleIdFromRedis(String articleId);
    /**
     * 获取Redis中存储的所有点赞数据
     * @return
     */
    List<ArticleLike> getLikedDataFromRedis();

    /**
     * 获取Redis中存储的所有点赞数量
     * @return
     */
    List<LikesAndViewCount> getLikedCountListFromRedis();

    List<LikesAndViewCount> getViewCountListFromRedis();
}

@Transactional(rollbackFor = Exception.class)
@Service
class RedisServiceImpl implements RedisService {

    @Autowired
    private HashOperations<String, String, Object> redisHash;

    @Override
    public Integer getLikeStatus(String articleId, String userId) {
        // 从redis中查询点赞状态
        // 若redis中存在点赞信息，则返回点赞状态
        if(redisHash.hasKey(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED, RedisKeyUtils.getLikedKey(articleId, userId))){
            HashMap<String, Object> map = (HashMap<String, Object>) redisHash.get(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED,
                                           RedisKeyUtils.getLikedKey(articleId, userId));
            return (Integer) map.get("status");
        }
        // 否则返回不存在
        else{
            return CONSTANT.LikedStatusEum.NOT_EXIST.getCode();
        }
    }

    @Override
    public Integer getViewStatus(String articleId) {
        // 从redis中查询浏览量信息
        // 若redis中存在浏览量信息，则返回浏览量信息
        if(redisHash.hasKey(RedisKeyUtils.MAP_KEY_ARTICLE_VIEW_COUNT, articleId)){
            return (Integer) redisHash.get(RedisKeyUtils.MAP_KEY_ARTICLE_VIEW_COUNT, articleId);
        }
        // 否则返回不存在
        else{
            return CONSTANT.LikedStatusEum.NOT_EXIST.getCode();
        }
    }

    @Override
    public Integer getLikeCountStatus(String articleId) {
        // 从redis中查询点赞量信息
        // 若redis中存在点赞量信息，则返回点赞量信息
        if(redisHash.hasKey(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED_COUNT, articleId)){
            return (Integer) redisHash.get(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED_COUNT, articleId);
        }
        // 否则返回不存在
        else{
            return CONSTANT.LikedStatusEum.NOT_EXIST.getCode();
        }
    }

    @Override
    public void saveLiked2Redis(String infoId, String likeUserId) {
        // 生成key
        String key = RedisKeyUtils.getLikedKey(infoId, likeUserId);
        // 封装value 喜欢状态
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",CONSTANT.LikedStatusEum.LIKE.getCode());

        redisHash.put(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED, key, map);
    }

    @Override
    public void saveUnLiked2Redis(String infoId, String likeUserId) {
        // 生成key
        String key = RedisKeyUtils.getLikedKey(infoId, likeUserId);
        // 封装value 不喜欢状态
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",CONSTANT.LikedStatusEum.UNLIKE.getCode());

        redisHash.put(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED, key, map);
    }

    @Override
    public void saveArticleLike2Redis(ArticleLike articleLike) {
        // 生成key
        String key = RedisKeyUtils.getLikedKey(articleLike.getArticle_id(), articleLike.getUser_id());
        // 封装value
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",articleLike.getStatus());

        redisHash.put(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED, key, map);
    }

    @Override
    public void saveLikedCount2Redis(String articleId, int likeCount) {
        redisHash.put(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED_COUNT, articleId, likeCount);
    }

    @Override
    public void saveViews2Redis(String articleId, int viewCount) {
        redisHash.put(RedisKeyUtils.MAP_KEY_ARTICLE_VIEW_COUNT, articleId, viewCount);
    }

    public void in_decrementLikedCount(String articleId, Integer delta){
        // 修改redis中的点赞变更数量
        redisHash.increment(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED_COUNT, articleId, delta);
    }

    @Override
    public int in_decrementViewCount(String articleId, Integer delta) {
        redisHash.increment(RedisKeyUtils.MAP_KEY_ARTICLE_VIEW_COUNT, articleId, delta);
        if(redisHash.hasKey(RedisKeyUtils.MAP_KEY_ARTICLE_VIEW_COUNT, articleId)){
            return (Integer) redisHash.get(RedisKeyUtils.MAP_KEY_ARTICLE_VIEW_COUNT, articleId);
        }
        else {
            return -1;
        }
    }

    @Override
    public List<ArticleLike> getLikedDataFromRedis() {
        // scan 读取数据，比key匹配优雅
        Cursor<Map.Entry<String, Object>> cursor = redisHash.scan(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED, ScanOptions.NONE);

        List<ArticleLike> list = new ArrayList<>();
        while (cursor.hasNext()){
            Map.Entry<String, Object> entry = cursor.next();
            String key = (String) entry.getKey();
            //分离出 articleId，userId, 解析value
            String[] split = key.split("::");
            String articleId = split[0];
            String likeUserId = split[1];
            HashMap<String, Object> map = (HashMap<String, Object>) entry.getValue();
            Integer status = (Integer) map.get("status");

            //组装成 UserLike 对象
            ArticleLike articleLike = new ArticleLike(articleId, likeUserId, status);
            list.add(articleLike);

            //存到 list 后从 Redis 中清理缓存
            redisHash.delete(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED, key);
        }
        return list;
    }

    @Override
    public List<LikesAndViewCount> getLikedCountListFromRedis() {
        // scan 读取数据，比key匹配优雅
        Cursor<Map.Entry<String, Object>> cursor = redisHash.scan(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED_COUNT, ScanOptions.NONE);
        List<LikesAndViewCount> list = new ArrayList<>();
        while (cursor.hasNext()){
            Map.Entry<String, Object> map = cursor.next();
            //将点赞数量存储在 LikedCountDT
            String key = (String)map.getKey();
            LikesAndViewCount likesAndViewCount = new LikesAndViewCount(key, (Integer) map.getValue());
            likesAndViewCount.setView_count(0);
            list.add(likesAndViewCount);
            //从Redis中删除这条记录
            redisHash.delete(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED_COUNT, key);
        }
        return list;
    }

    @Override
    public List<LikesAndViewCount> getViewCountListFromRedis() {
        // scan 读取数据，比key匹配优雅
        Cursor<Map.Entry<String, Object>> cursor = redisHash.scan(RedisKeyUtils.MAP_KEY_ARTICLE_VIEW_COUNT, ScanOptions.NONE);
        List<LikesAndViewCount> list = new ArrayList<>();
        while (cursor.hasNext()){
            Map.Entry<String, Object> map = cursor.next();
            //将点赞数量存储在 ViewCountDT
            String key = (String)map.getKey();
            LikesAndViewCount likesAndViewCount = new LikesAndViewCount();
            likesAndViewCount.setArticle_id(key);
            likesAndViewCount.setLike_count(0);
            likesAndViewCount.setView_count((Integer) map.getValue());
            list.add(likesAndViewCount);
            //从Redis中删除这条记录
            redisHash.delete(RedisKeyUtils.MAP_KEY_ARTICLE_VIEW_COUNT, key);
        }
        return list;
    }

    @Override
    public int getLikedCountByArticleIdFromRedis(String articleId) {
        if(redisHash.hasKey(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED_COUNT, articleId)){
            return (Integer) redisHash.get(RedisKeyUtils.MAP_KEY_ARTICLE_LIKED_COUNT, articleId);
        }
        else{
            return 0;
        }
    }
}
