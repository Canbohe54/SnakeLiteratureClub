package com.snach.literatureclub.utils;

import com.snach.literatureclub.common.DatabaseServiceType;
import com.snach.literatureclub.utils.redis.RedisConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;

public class IdManager {
    private static final DatabaseServiceType serviceType = DatabaseServiceType.COMMON;

    @Value("${snach.common.redisKeyOfCurrentUserId}")
    private String redisKeyNameOfCurrentUserId = "CURRENT_USER_ID";

    private static Jedis jedis;

    private static volatile IdManager manager;

    private IdManager() {}

    public static IdManager getManager() {
        if (manager == null) {
            synchronized (IdManager.class) {
                if (manager == null) {
                    manager = new IdManager();
                    RedisConnectionFactory connectionFactory = RedisConnectionFactory.getConnectionFactory();
                    jedis = connectionFactory.getJedis(serviceType);
                }
            }
        }
        return manager;
    }

    public synchronized String generateUserId() {
        long newId = jedis.incr(redisKeyNameOfCurrentUserId);
        if (1e11 <= newId && newId < 2e11) {
            newId = (long) 2e11;
            jedis.set(redisKeyNameOfCurrentUserId, String.valueOf(newId));
        }
        return String.valueOf(newId);
    }

    public synchronized String generateArticleId() {
        return String.valueOf(System.currentTimeMillis());
    }

    public synchronized String generateCommentId() {
        return String.valueOf(System.currentTimeMillis());
    }
}
