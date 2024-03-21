package com.snach.literatureclub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snach.literatureclub.common.DatabaseService;
import com.snach.literatureclub.utils.RedisConnectionFactory;
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
    private static final DatabaseService serviceType = DatabaseService.COMMON;

    @Value("${snach.common.redisKeyNameOfArticleTags}")
    private String redisKeyNameOfArticleTags;

    private static final RedisConnectionFactory connectionFactory;

    static {
        connectionFactory = RedisConnectionFactory.getConnectionFactory();
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
