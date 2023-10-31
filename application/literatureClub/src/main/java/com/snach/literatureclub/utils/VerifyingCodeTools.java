package com.snach.literatureclub.utils;

import com.snach.literatureclub.service.MailService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import static com.snach.literatureclub.utils.IdTools.*;

@Component
public class VerifyingCodeTools {
    @Autowired
    private MailService mailService;

    private static MailService staticMailService;

    private static final JedisPool jedispool;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(20);
        config.setMinIdle(5);
        jedispool = new JedisPool(config, "localhost", 6379);
    }

    @PostConstruct
    public void init() {
        staticMailService = mailService;
    }

    public static boolean sendVerifyingCode(String mail) {
        String vCode = randomStringGen(6);
        Jedis jedis = jedispool.getResource();
        jedis.set(mail, vCode);
        jedis.expire(mail, 600);
        jedis.close();
        try {
            staticMailService.sendVerifyingCode(mail, vCode);
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean verifyCode(String mail, String code) {
        Jedis jedis = jedispool.getResource();
        String vCode = jedis.get(mail);
        return vCode != null && vCode.equals(code);
    }
}
