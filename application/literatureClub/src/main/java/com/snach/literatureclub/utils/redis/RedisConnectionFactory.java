package com.snach.literatureclub.utils.redis;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.snach.literatureclub.common.DatabaseServiceType;

@Component
public class RedisConnectionFactory {
    private static final Logger logger = LoggerFactory.getLogger(RedisConnectionFactory.class);

    private static volatile RedisConnectionFactory connectionFactory;

    private static JedisPool jedisPool;

    private static Map<DatabaseServiceType, Integer> redisDatabaseNumberMap;

    private RedisConnectionFactory() {}

    @Autowired
    private void loadJedisPool(JedisPool pool) {
        RedisConnectionFactory.jedisPool = pool;
    }

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

    public Jedis getJedis(DatabaseServiceType service) {
        if (redisDatabaseNumberMap == null) {
            loadRedisDatabaseNumberMap();
        }
        Integer databaseNumber = redisDatabaseNumberMap.get(service);
        if (databaseNumber == null) {
            logger.warn("Personal redisDatabaseNumberMap has no DatabaseServiceType." + service + " value. Use DEFAULT value.");
            databaseNumber = redisDatabaseNumberMap.get(DatabaseServiceType.DEFAULT);
        }
        Jedis jedis = jedisPool.getResource();
        jedis.select(databaseNumber);
        return jedis;
    }

    private static void loadRedisDatabaseNumberMap() {
        redisDatabaseNumberMap = new HashMap<>();
        for (DatabaseServiceType DatabaseServiceType : DatabaseServiceType.values()) {
            redisDatabaseNumberMap.put(DatabaseServiceType, DatabaseServiceType.ordinal());
        }

        logger.info("Load default redisDatabaseNumberMap.");
    }

    public static void loadRedisDatabaseNumberMap(Map<DatabaseServiceType, Integer> databaseNumberMap) {
        redisDatabaseNumberMap = databaseNumberMap;
        Integer defaultDatabaseNumber = databaseNumberMap.get(DatabaseServiceType.DEFAULT);
        if (defaultDatabaseNumber == null) {
            redisDatabaseNumberMap.put(DatabaseServiceType.DEFAULT, 0);
            logger.warn("Personal redisDatabaseNumberMap has no DatabaseServiceType.DEFAULT value. Use 0 as default.");
        }
    }
}
