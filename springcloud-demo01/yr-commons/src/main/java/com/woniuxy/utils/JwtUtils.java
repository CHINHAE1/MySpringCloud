package com.woniuxy.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: 马宇航
 * @Todo: JWT工具类，生成token，验证token，获得token中的信息无需secret解密也能获得，AI生成工具，百度都有源码
 * @DateTime: 25/04/09/星期三 15:12
 * @Component: 成都蜗牛学苑
 **/
@Slf4j
@Component
public class JWTUtils {
    //内部定义枚举类
    public enum TokenStatusEnum{
        TOKEN_EXPIRE,
        TOKEN_ERROR,//被人篡改过token
        TOKEN_SUCCESS;
    }
    //统一秘钥定义
    private static final String SECURY_KEY="wonixuy123123";
    //过期时间 正常的jwt的过期时间
    private static final Long TOKEN_EXPIRE_TIME= 10 * 60 * 1000L; //10MIN -->企业中是30min
    //双token的，刷新token 时间会比较长，面试容易被问到的点
    public static final Long REFRESH_TOKEN_EXPIRE_TIME=30 * 60 * 1000L; //30MIN -->企业中 60min
    //加密算法固定
    private static Algorithm algorithm = Algorithm.HMAC256(SECURY_KEY);
    /**
     * 生成token
     * ChangeLog : 1. 创建 (25/01/08/0008 09:51 [马宇航]);
     * @param username  登录成功的用户名
     * @return java.lang.String
     */
    public static String createToken(String username){
        Date now = new Date();
        return JWT.create()
                .withIssuer("mayuhang") //签发人
                .withIssuedAt(now) //签发时间
                .withClaim("username",username) //payload 自定义的用户数据 明文
                .withExpiresAt(new Date(now.getTime() + TOKEN_EXPIRE_TIME)) //过期时间
                .sign(algorithm);
    }
    /**
     * token验证
     * ChangeLog : 1. 创建 (25/01/08/0008 09:57 [马宇航]);
     * @param token
     * @return com.woniuxy.utils.JWTUtils.TokenStatusEnum
     */
    public static TokenStatusEnum verify(String token){
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("mayuhang")
                    .build();
            verifier.verify(token);
            return TokenStatusEnum.TOKEN_SUCCESS;
        }catch (TokenExpiredException e){
            //过期异常会在之前先进行校验
            return TokenStatusEnum.TOKEN_EXPIRE;
        }catch (JWTVerificationException e){
            //这个异常 就直接是 token 验证不通过的异常
            return TokenStatusEnum.TOKEN_ERROR;
        }
    }
    /**
     * Description : 获得token中的信息无需secret解密也能获得  <br/>
     * ChangeLog : 1. 创建 (2021/5/3 22:01 [mayuhang]);
     * @param token
     * @return token中包含的用户名
     **/
    public static String getUserName(String token){
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        }catch (JWTDecodeException e){
            return TokenStatusEnum.TOKEN_ERROR.name();
        }
    }
} 