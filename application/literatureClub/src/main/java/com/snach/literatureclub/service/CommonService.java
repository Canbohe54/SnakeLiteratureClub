package com.snach.literatureclub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snach.literatureclub.common.DatabaseServiceType;
import com.snach.literatureclub.utils.redis.RedisConnectionFactory;
import jakarta.annotation.PostConstruct;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Map;

@Service
public interface CommonService {
    Map<String, Object> loadArticleTags();
}

@Service
class CommonServiceImpl implements CommonService {
    private static final DatabaseServiceType serviceType = DatabaseServiceType.COMMON;

    // the key of common data in redis
    @Value("${snach.common.redisKeyOfArticleTags:ARTICLE_TAGS}")
    private String redisKeyNameOfArticleTags;
    @Value("${snach.common.redisKeyOfCurrentUserId:CURRENT_USER_ID}")
    private String redisKeyNameOfCurrentUserId;

    private RedisConnectionFactory connectionFactory;

    @Autowired
    private void loadRedisConnectionFactory(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @PostConstruct
    private void redisStorageInitialize() {
        connectionFactory = RedisConnectionFactory.getConnectionFactory();
        try (Jedis jedis = connectionFactory.getJedis(serviceType)) {
            jedis.setnx(redisKeyNameOfArticleTags, "{}");
            jedis.setnx(redisKeyNameOfCurrentUserId, "10001");
        }
    }

    public Map<String, Object> loadArticleTags() {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        String tags = jedis.get(redisKeyNameOfArticleTags);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> parsedTags = null;
        try {
            parsedTags = mapper.readValue(tags, Map.class);
        } catch (Exception ignored) {

        }
        return parsedTags;
    }
}
