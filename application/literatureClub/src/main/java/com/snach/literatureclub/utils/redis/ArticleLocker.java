package com.snach.literatureclub.utils.redis;

import com.snach.literatureclub.common.DatabaseServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

import java.util.List;

@Configuration
public class ArticleLocker {
    private static final DatabaseServiceType serviceType = DatabaseServiceType.ARTICLE_LOCK;

    private final RedisConnectionFactory connectionFactory;

    @Autowired
    public ArticleLocker(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public synchronized void lock(String articleId, long expire, List<String> su) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        jedis.sadd(articleId, su.toArray(new String[0]));
        jedis.expire(articleId, expire);
    }

    public synchronized void lock(String articleId, long expire, String... su) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        jedis.sadd(articleId, su);
        jedis.expire(articleId, expire);
    }

    public synchronized void unlock(String articleId) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        jedis.del(articleId);
    }

    public synchronized boolean checkLock(String articleId) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        return jedis.exists(articleId);
    }

    public synchronized boolean checkLockPermission(String articleId, String userId) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        return jedis.sismember(articleId, userId);
    }

    public synchronized long getArticleLockExpire(String articleId) {
        Jedis jedis = connectionFactory.getJedis(serviceType);
        return jedis.ttl(articleId);
    }
}
