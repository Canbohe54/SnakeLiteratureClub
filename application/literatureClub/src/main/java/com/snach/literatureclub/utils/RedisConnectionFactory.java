package com.snach.literatureclub.utils;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.snach.literatureclub.common.DatabaseService;

@Component
public class RedisConnectionFactory {
    private static final Logger logger = LoggerFactory.getLogger(RedisConnectionFactory.class);

    private static volatile RedisConnectionFactory connectionFactory;

    @Resource
    private JedisPool jedisPool;

    private static Map<DatabaseService, Integer> redisDatabaseNumberMap;

    private RedisConnectionFactory() {}

    public static RedisConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            synchronized (RedisConnectionFactory.class) {
                if (connectionFactory == null) {
                    connectionFactory = new RedisConnectionFactory();
                }
            }
        }
        return connectionFactory;
    }

    public Jedis getJedis(DatabaseService service) {
        if (redisDatabaseNumberMap == null) {
            loadRedisDatabaseNumberMap();
        }
        Integer databaseNumber = redisDatabaseNumberMap.get(service);
        if (databaseNumber == null) {
            logger.warn("Personal redisDatabaseNumberMap has no DatabaseService." + service + " value. Use DEFAULT value.");
            databaseNumber = redisDatabaseNumberMap.get(DatabaseService.DEFAULT);
        }
        Jedis jedis = jedisPool.getResource();
        jedis.select(databaseNumber);
        return jedis;
    }

    private void loadRedisDatabaseNumberMap() {
        redisDatabaseNumberMap = new HashMap<>();
        for (DatabaseService databaseService : DatabaseService.values()) {
            redisDatabaseNumberMap.put(databaseService, databaseService.ordinal());
        }

        logger.info("Load default redisDatabaseNumberMap.");
    }

    public void loadRedisDatabaseNumberMap(Map<DatabaseService, Integer> databaseNumberMap) {
        redisDatabaseNumberMap = databaseNumberMap;
        Integer defaultDatabaseNumber = databaseNumberMap.get(DatabaseService.DEFAULT);
        if (defaultDatabaseNumber == null) {
            redisDatabaseNumberMap.put(DatabaseService.DEFAULT, 0);
            logger.warn("Personal redisDatabaseNumberMap has no DatabaseService.DEFAULT value. Use 0 as default.");
        }
    }
}
