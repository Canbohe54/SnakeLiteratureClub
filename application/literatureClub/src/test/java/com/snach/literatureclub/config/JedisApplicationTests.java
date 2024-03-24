package com.snach.literatureclub.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Jedis;

@SpringBootTest
public class JedisApplicationTests {
    @Autowired
    private JedisPool jedisPool;

    @Test
    public void contextLoads() {
        System.out.println(jedisPool);
        //在连接池中得到Jedis连接
        Jedis jedis = jedisPool.getResource();
        jedis.set("haha", "你好");
        jedis.set("name", "snach");
        //关闭当前连接
    }
}
