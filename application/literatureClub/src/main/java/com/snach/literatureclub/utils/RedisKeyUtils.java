package com.snach.literatureclub.utils;

public class RedisKeyUtils {
    /**
     *
     保存用户点赞内容数据的key
     */
    public static final String MAP_KEY_ARTICLE_LIKED = "MAP_ARTICLE_LIKED";
    /**
     *
     保存内容被点赞数量的key
     */
    public static final String MAP_KEY_ARTICLE_LIKED_COUNT = "MAP_ARTICLE_LIKED_COUNT";

    public static final String MAP_KEY_ARTICLE_VIEW_COUNT = "MAP_KEY_ARTICLE_VIEW_COUNT";

    /**
     * 拼接被点赞的内容id和点赞的人的id作为key。格式 222222::333333
     * @param articleId 被点赞的内容 id
     * @param userId 点赞的人的id
     * @return
     */
    public static String getLikedKey(String articleId, String userId){
        return articleId + "::" + userId;
    }
}
