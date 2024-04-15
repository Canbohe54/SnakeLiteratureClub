package com.snach.literatureclub.utils;

import com.snach.literatureclub.common.DatabaseServiceType;
import com.snach.literatureclub.utils.redis.RedisConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

import java.util.Random;

@Configuration
public class IdManager {
    private static final DatabaseServiceType serviceType = DatabaseServiceType.COMMON;

    @Value("${snach.common.redisKeyOfCurrentUserId:CURRENT_USER_ID}")
    private String redisKeyNameOfCurrentUserId;
    private static final String code62 = "WLJwjm8F6iySOH2aXvK4RnZ1Nkbel93cQoYgzGE0rChVIMdDTPs5UAfqt7uBxp";
    private static final String code10 = "0123456789";
    private static final Random random = new Random();
    private final RedisConnector connectionFactory;

    @Autowired
    public IdManager(RedisConnector connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public static String randomStringGen62(int length) {
        StringBuilder randStr = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randStr.append(code62.charAt(random.nextInt(code62.length())));
        }
        return randStr.toString();
    }

    public static String randomStringGen10(int length) {
        StringBuilder randStr = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randStr.append(code10.charAt(random.nextInt(code10.length())));
        }
        return randStr.toString();
    }

    public static String numberConvert(long timeStamp) {
        StringBuilder stamp = new StringBuilder();
        while (timeStamp >= code62.length()) {
            stamp.append(code62.charAt((int) (timeStamp % code62.length())));
            timeStamp = timeStamp / code62.length();
        }
        stamp.append(code62.charAt((int) (timeStamp % code62.length())));
        stamp.append(randomStringGen62(4));
        return stamp.toString();
    }

    public String generateUserId() {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        long newId = jedis.incr(redisKeyNameOfCurrentUserId);
        if (1e11 <= newId && newId < 2e11) {
            newId = (long) 2e11;
            jedis.set(redisKeyNameOfCurrentUserId, String.valueOf(newId));
        }
        return String.valueOf(newId);
    }

    public String generateNormalId() {
        return numberConvert(System.currentTimeMillis());
    }

    public String generateArticleId() {
        return "a" + generateNormalId();
    }

    public String generateCommentId() {
        return "c" + generateNormalId();
    }

    public String generateMessageId() {
        return "m" + generateNormalId();
    }

    public String generateTagId() { return "t" + generateNormalId(); }
}
