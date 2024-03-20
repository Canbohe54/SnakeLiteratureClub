package com.snach.literatureclub.utils;

import com.snach.literatureclub.common.DatabaseService;
import redis.clients.jedis.Jedis;

public class ArticleLocker {
    private static final DatabaseService serviceType = DatabaseService.ARTICLE_LOCK;

    private static volatile ArticleLocker locker;

    private static RedisConnectionFactory connectionFactory;

    private ArticleLocker() {}

    public static ArticleLocker getInstance() {
        if (locker == null) {
            synchronized (RedisConnectionFactory.class) {
                if (locker == null) {
                    locker = new ArticleLocker();
                    connectionFactory = RedisConnectionFactory.getConnectionFactory();
                }
            }
        }
        return locker;
    }

    public synchronized void lock(String articleId, long expire) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        jedis.setex(articleId, expire, "<locked>");
        jedis.close();
    }

    public synchronized void lock(String articleId, long expire, String message) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        jedis.setex(articleId, expire, message);
        jedis.close();
    }

    public synchronized String getLockMessage(String articleId) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        String message = jedis.get(articleId);
        jedis.close();
        return message;
    }

    public synchronized void unlock(String articleId) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        jedis.del(articleId);
        jedis.close();
    }

    public synchronized boolean checkLock(String articleId) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        boolean isExist = jedis.exists(articleId);
        jedis.close();
        return isExist;
    }
}
