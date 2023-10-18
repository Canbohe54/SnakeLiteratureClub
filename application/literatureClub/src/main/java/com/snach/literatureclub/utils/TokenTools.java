package com.snach.literatureclub.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.snach.literatureclub.bean.BaseUser;

import java.util.Calendar;
import java.util.Map;

public class TokenTools {
    private static final String SECRET = "@#Hnun)-";

    private static Map<String, Object> HEADERS;

    static {
        HEADERS.put("alg", "HS256");
        HEADERS.put("typ", "JWT");
    }

    public static String tokenGen(BaseUser user) {
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.SECOND, 60);
        return JWT.create()
                .withHeader(HEADERS)
                .withClaim("id", user.getId())
                .withExpiresAt(expires.getTime())
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static boolean tokenVerify(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            return false;
        }
        return true;
    }

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
}
