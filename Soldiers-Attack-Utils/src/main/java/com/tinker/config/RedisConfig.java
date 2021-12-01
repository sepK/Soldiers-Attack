package com.tinker.config;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * 显式的重写Redis
 *
 * @author bailihong
 * @version v1.0
 * @date 2020/5/24 13:12
 */
@Configuration
public class RedisConfig {

    /**
     * 重写LettuceConnectionFactory
     *
     * @param redisProperties Redis的配置参数
     * @return LettuceConnectionFactory
     */
    @Bean
    @Primary
    public LettuceConnectionFactory lettuceConnectionFactory(RedisProperties redisProperties) {
        //redis配置
        RedisConfiguration redisConfiguration = new
                RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        ((RedisStandaloneConfiguration) redisConfiguration).setDatabase(redisProperties.getDatabase());
        ((RedisStandaloneConfiguration) redisConfiguration).setPassword(redisProperties.getPassword());

        //连接池配置
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(redisProperties.getMaxIdle());
        genericObjectPoolConfig.setMinIdle(redisProperties.getMinIdle());
        genericObjectPoolConfig.setMaxTotal(redisProperties.getMaxActive());
        genericObjectPoolConfig.setMaxWaitMillis(redisProperties.getMaxWait());

        //redis客户端配置
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder
                builder = LettucePoolingClientConfiguration.builder().
                commandTimeout(Duration.ofMillis(redisProperties.getTimeOut()));

        builder.shutdownTimeout(Duration.ofMillis(redisProperties.getShutdownTimeOut()));
        builder.poolConfig(genericObjectPoolConfig);
        if (redisProperties.isSsl()) {
            builder.useSsl();
        }
        LettuceClientConfiguration lettuceClientConfiguration = builder.build();

        //根据配置和客户端配置创建连接
        LettuceConnectionFactory lettuceConnectionFactory = new
                LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration);


        return lettuceConnectionFactory;
    }


    /**
     * 重写redisTemplate
     *
     * @return RedisTemplate
     */
    @SuppressWarnings("unchecked")
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        template.setKeySerializer(keySerializer());
        template.setHashKeySerializer(keySerializer());
        template.setValueSerializer(valueSerializer());
        template.setHashValueSerializer(valueSerializer());

        return template;
    }

    /**
     * 缓存存取处理对象
     *
     * @return CacheManager
     */
    @Primary
    @Bean
    public CacheManager cacheManager(RedisProperties redisProperties, LettuceConnectionFactory lettuceConnectionFactory) {
        //缓存配置对象
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.entryTtl(
                Duration.ofDays(redisProperties.getExpire())
        ).disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer((valueSerializer())));
        return RedisCacheManager.builder(
                RedisCacheWriter.nonLockingRedisCacheWriter(lettuceConnectionFactory)
        ).cacheDefaults(redisCacheConfiguration).build();
    }

    private StringRedisSerializer keySerializer() {
        return new StringRedisSerializer();
    }

    private GenericJackson2JsonRedisSerializer valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

}
