package com.snach.literatureclub.utils;

import com.snach.literatureclub.common.DatabaseServiceType;
import com.snach.literatureclub.utils.redis.RedisConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class IdManager {
    private static final DatabaseServiceType serviceType = DatabaseServiceType.COMMON;

    @Value("${snach.common.redisKeyOfCurrentUserId:CURRENT_USER_ID}")
    private String redisKeyNameOfCurrentUserId;

    private final RedisConnector connectionFactory;

    @Autowired
    public IdManager(RedisConnector connectionFactory) {
        this.connectionFactory = connectionFactory;
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
