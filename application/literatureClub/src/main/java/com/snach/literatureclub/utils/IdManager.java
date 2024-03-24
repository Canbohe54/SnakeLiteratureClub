package com.snach.literatureclub.utils;

import com.snach.literatureclub.common.DatabaseServiceType;
import com.snach.literatureclub.utils.redis.RedisConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;

public class IdManager {
    private static final DatabaseServiceType serviceType = DatabaseServiceType.COMMON;

    @Value("${snach.common.redisKeyOfCurrentUserId:CURRENT_USER_ID}")
    private String redisKeyNameOfCurrentUserId;

    private static RedisConnectionFactory connectionFactory;

    private static volatile IdManager manager;

    private IdManager() {}

    public static IdManager getManager() {
        if (manager == null) {
            synchronized (IdManager.class) {
                if (manager == null) {
                    manager = new IdManager();
                    connectionFactory = RedisConnectionFactory.getConnectionFactory();
                }
            }
        }
        return manager;
    }

    public synchronized String generateUserId() {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        long newId = jedis.incr(redisKeyNameOfCurrentUserId);
        if (1e11 <= newId && newId < 2e11) {
            newId = (long) 2e11;
            jedis.set(redisKeyNameOfCurrentUserId, String.valueOf(newId));
        }
        return String.valueOf(newId);
    }

    @SuppressWarnings("deprecation")
    public synchronized String generateArticleId() {
        return IdTools.generateId(IdTools.Type.ARTICLE);
//        return String.valueOf(System.currentTimeMillis());
    }

    @SuppressWarnings("deprecation")
    public synchronized String generateCommentId() {
        return IdTools.generateId(IdTools.Type.COMMENT);
//        return String.valueOf(System.currentTimeMillis());
    }
}
