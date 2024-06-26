package com.snach.literatureclub.utils.redis;

import com.snach.literatureclub.common.ArticleStatisticalDataType;
import com.snach.literatureclub.common.DatabaseServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class ArticleDataStatistician {
    private static final DatabaseServiceType serviceType = DatabaseServiceType.ARTICLE_STATISTICAL_DATA;

    private final RedisConnector connectionFactory;

    @Autowired
    public ArticleDataStatistician(RedisConnector connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Get the statistical data of the article
     *
     * @param articleId           the article id
     * @param statisticalDataType the statistical data type
     * @return the statistical data
     */
    public synchronized long getStatisticalData(String articleId, ArticleStatisticalDataType statisticalDataType) {
        try (Jedis jedis = connectionFactory.getJedis(serviceType)) {
            String statisticalData = jedis.hget(articleId, statisticalDataType.name());
            return Long.parseLong(statisticalData);
        }
    }

    /**
     * Add value to the statistical data
     *
     * @param articleId           the article id
     * @param statisticalDataType the statistical data type
     * @param value               the value added on the statistical data
     */
    public synchronized void addStatisticalData(String articleId, ArticleStatisticalDataType statisticalDataType, long value) {
        try (Jedis jedis = connectionFactory.getJedis(serviceType)) {
            jedis.hincrBy(articleId, statisticalDataType.name(), value);
        }
    }

    /**
     * Add 1 to the statistical data
     *
     * @param articleId           the article id
     * @param statisticalDataType the statistical data type
     */
    public synchronized void addStatisticalData(String articleId, ArticleStatisticalDataType statisticalDataType) {
        addStatisticalData(articleId, statisticalDataType, 1);
    }

    /**
     * Set value to the statistical data
     *
     * @param articleId           the article id
     * @param statisticalDataType the statistical data type
     * @param value               the new value of the statistical data
     */
    public void setStatisticalData(String articleId, ArticleStatisticalDataType statisticalDataType, long value) {
        try (Jedis jedis = connectionFactory.getJedis(serviceType)) {
            jedis.hset(articleId, statisticalDataType.name(), String.valueOf(value));
        }
    }
}
