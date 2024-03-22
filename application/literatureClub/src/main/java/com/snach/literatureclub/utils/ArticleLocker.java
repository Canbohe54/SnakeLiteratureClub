package com.snach.literatureclub.utils;

import com.snach.literatureclub.common.DatabaseServiceType;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.List;

@Component
public class ArticleLocker {
    private static final DatabaseServiceType serviceType = DatabaseServiceType.ARTICLE_LOCK;

    private static volatile ArticleLocker locker;

    private static RedisConnectionFactory connectionFactory;

    private ArticleLocker() {}

    @Bean
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

    public synchronized void lock(String articleId, long expire, List<String> su) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        jedis.sadd(articleId, su.toArray(new String[0]));
        jedis.expire(articleId, expire);
        jedis.close();
    }

    public synchronized void lock(String articleId, long expire, String... su) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        jedis.sadd(articleId, su);
        jedis.expire(articleId, expire);
        jedis.close();
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

    public synchronized boolean checkLockPermission(String articleId, String userId) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        boolean isExist = jedis.sismember(articleId, userId);
        jedis.close();
        return isExist;
    }
}
