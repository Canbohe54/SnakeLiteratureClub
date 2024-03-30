package com.snach.literatureclub.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.snach.literatureclub.bean.User;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TokenTools {
    // 加密时的`盐`，后续可使用yaml文件配置
    private static final String SECRET = "@#Hnun)-";
    private static final String HARD_SECRET = "#@)(*G~1";

    private static final Map<String, Object> HEADERS;

    static {
        HEADERS = new HashMap<>();
        HEADERS.put("alg", "HS256");
        HEADERS.put("typ", "JWT");
    }

    /**
     * Generate user JWT-token by a User object
     * @param user a User object
     * @return JWT-token
     */
    public static String tokenGen(User user) {
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.MINUTE, 60);
        return JWT.create()
                .withHeader(HEADERS)
                .withClaim("id", user.getId())
                .withExpiresAt(expires.getTime())
                .sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * Generate user JWT-token by a User id
     * @param id user id
     * @return JWT-token
     */
    public static String tokenGen(String id) {
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.MINUTE, 60);
        return JWT.create()
                .withHeader(HEADERS)
                .withClaim("id", id)
                .withExpiresAt(expires.getTime())
                .sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 生成关键操作JWT
     *
     * @param userid 用户
     * @return JWT字符串
     */
    public static String hardTokenGen(String userid) {
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.MINUTE, 5);
        return JWT.create()
                .withHeader(HEADERS)
                .withClaim("id", userid)
                .withExpiresAt(expires.getTime())
                .sign(Algorithm.HMAC256(HARD_SECRET));
    }

    /**
     * Verify is the token valid
     * @param token JWT-token
     * @return true if valid
     */
    public static boolean tokenVerify(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            return false;
        }
        return true;
    }

    /**
     * Verify if the token is valid and belongs to the user
     * @param token JWT-token
     * @param user a user object
     * @return true if valid
     */
    public static boolean tokenVerify(String token, User user) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            return false;
        }
        return Objects.equals(getPayload(token, "id"), user.getId());
    }

    public static boolean tokenVerify(String token, String userId) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            return false;
        }
        return Objects.equals(getPayload(token, "id"), userId);
    }

    public static boolean hardTokenVerify(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(HARD_SECRET)).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            return false;
        }
        return true;
    }

    /**
     * Get the value of a payload-field of a verified JWT-token
     * @param token JWT-token
     * @param key the key of payload
     * @return the value of the key
     */
    public static String getPayload(String token, String key) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT decodedJWT;
        try {
            decodedJWT = jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
        return decodedJWT.getClaim(key).asString();
    }

    public static String getHardPayload(String token, String key) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(HARD_SECRET)).build();
        DecodedJWT decodedJWT;
        try {
            decodedJWT = jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
        return decodedJWT.getClaim(key).asString();
    }
}
