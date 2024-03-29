package com.snach.literatureclub.utils.redis;

import com.snach.literatureclub.common.DatabaseServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

@Component
public class RedisConnector {
    private static final Logger logger = LoggerFactory.getLogger(RedisConnector.class);

    private static Map<DatabaseServiceType, Integer> redisDatabaseNumberMap;

    private final JedisPool jedisPool;

    @Autowired
    public RedisConnector(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
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
