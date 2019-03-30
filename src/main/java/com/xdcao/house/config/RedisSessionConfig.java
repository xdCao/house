package com.xdcao.house.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @Author: buku.ch
 * @Date: 2019-03-29 23:15
 */

/*session会话*/
@Configuration
@EnableRedisHttpSession
public class RedisSessionConfig {

    @Bean
    public RedisTemplate<String,String> redisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }



}
