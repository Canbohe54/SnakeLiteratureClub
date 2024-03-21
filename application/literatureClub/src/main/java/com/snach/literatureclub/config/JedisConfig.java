package com.snach.literatureclub.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisConfig {
    private final Logger logger = LoggerFactory.getLogger(JedisConfig.class);

    @Value("${snach.datasource.redis.host}")
    private String host;

    @Value("${snach.datasource.redis.port}")
    private int port;

    @Value("${snach.datasource.redis.timeout}")
    private int timeout;

    @Value("${snach.datasource.redis.pool.max-active}")
    private int maxActive;

    @Value("${snach.datasource.redis.pool.max-idle}")
    private int maxIdle;

    @Value("${snach.datasource.redis.pool.min-idle}")
    private int minIdle;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setJmxEnabled(false);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);

        logger.info("JedisPool(" + host + ":" + port + ") connect successfully");
        return jedisPool;
    }
}