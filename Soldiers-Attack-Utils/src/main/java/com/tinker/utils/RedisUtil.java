package com.tinker.utils;


import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Description: Redis工具类
 * <br/>
 * RedisUtil
 *
 * @author laiql
 * @date 2021/3/25 10:35 上午
 */
@Component
public class RedisUtil {

    private static final Long SUCCESS = 1L;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 保存对象
     *
     * @param key   键
     * @param value 值
     * @return Boolean
     */
    public Boolean set(String key, Object value) {
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        operations.set(key, value);
        return true;
    }

    /**
     * 带过期时间的保存对象
     *
     * @param key        键
     * @param value      值
     * @param expireTime 过期时间
     * @param timeUnit   时间单位
     * @return Boolean
     */
    public Boolean set(String key, Object value, Long expireTime, TimeUnit timeUnit) {
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        operations.set(key, value, expireTime, timeUnit);
        return true;
    }

    /**
     * 查看是否存在指定键
     *
     * @param key 键
     * @return Boolean
     */
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 通过指定键获取对象
     *
     * @param key 键
     * @return java.lang.Object
     */
    public Object get(String key) {
        Object result;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 返回所有符合条件的key
     *
     * @param patten 正则表达式
     * @return Set
     */
    public Set<String> keys(String patten) {
        return redisTemplate.keys(patten);
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 获取指定Key的过期时间
     *
     * @param key
     * @return 秒    分钟*60
     */
    public Long getKeyExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey    锁
     * @param requestId  请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public Boolean setDistributedLock(String lockKey, String requestId, long expireTime) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLock.lua")));
        RedisSerializer argsSerializer = new StringRedisSerializer();
        RedisSerializer resultSerializer = new StringRedisSerializer();
        Object result = redisTemplate.execute(redisScript, argsSerializer, resultSerializer, Collections.singletonList(lockKey), requestId, expireTime + "");
        return SUCCESS.equals(result);
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public Boolean releaseDistributedLock(String lockKey, String requestId) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/releaseLock.lua")));
        RedisSerializer argsSerializer = new StringRedisSerializer();
        RedisSerializer resultSerializer = new StringRedisSerializer();
        Object result = redisTemplate.execute(redisScript, argsSerializer, resultSerializer, Collections.singletonList(lockKey), requestId);
        return SUCCESS.equals(result);
    }

    /**
     * 分布式自增队列
     *
     * @param key key
     * @return Long
     */
    public Long generate(String key) {
        return atomicIncrement(key);
    }

    /**
     * 重置队列
     *
     * @param key key
     */
    public void resetSequence(String key) {
        new RedisAtomicLong(key, redisTemplate.getConnectionFactory(), 0);
    }

    /**
     * 初始化分布式自增队列
     *
     * @param key
     * @param val
     */
    public void atomicInit(String key, Long val) {
        new RedisAtomicLong(key, redisTemplate.getConnectionFactory(), val);
    }

    /**
     * 分布式自增队列
     *
     * @param key key
     * @return Long
     */
    public Long atomicIncrement(String key) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return counter.incrementAndGet();
    }

    /**
     * 分布式自减队列
     *
     * @param key key
     * @return Long
     */
    public Long atomicDecrement(String key) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return counter.decrementAndGet();
    }

    /**
     * 分布式队列增加值
     *
     * @param key
     * @param delta
     * @return
     */
    public Long atomicAddAndGet(String key, Long delta) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return counter.addAndGet(delta);
    }

    /**
     * 分布式队列设置值
     *
     * @param key
     * @param delta
     * @return
     */
    public Long atomicGetAndSet(String key, Long delta) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return counter.getAndSet(delta);
    }

    /**
     * 加锁等待，执行指定的callable 操作
     * 当指定操作已经被加锁时，尝试等待，每 100ms 尝试一次，直到 expireTime
     *
     * @param callable   需要执行的操作
     * @param error      出现异常的消费
     * @param key        锁名
     * @param expireTime 等待时间，锁的过期时间，单位<font size=8 color="red">毫秒</font>
     * @return callable 返回的可空数据 Optional<T>
     */
    public <T> Optional<T> lockfor(Callable<T> callable, Consumer<Exception> error, String key, long expireTime) {
        boolean isLock = true;
        String requestId = UUID.randomUUID().toString();
        long waitTime = 500;
        long hadWaitedTime = 0;
        try {
            do {
                isLock = setDistributedLock(key, requestId, expireTime / 1000);
                if (isLock) {
                    return Optional.ofNullable(callable.call());
                } else if (expireTime - hadWaitedTime > 0) {
                    Thread.sleep(waitTime);
                    hadWaitedTime = hadWaitedTime + waitTime;
                } else {
                    break;
                }
            } while (!isLock);
            error.accept(new TimeoutException("Error occurred while request a lock for " + key + ". Time expired."));
            return Optional.empty();
        } catch (Exception e) {
            if (error != null) {
                error.accept(e);
            }
            return Optional.empty();
        } finally {
            if (isLock) {
                releaseDistributedLock(key, requestId);
            }
        }
    }

    /**
     * 加锁等待，执行指定的callable 操作
     * 当指定操作已经被加锁时，尝试等待，每 100ms 尝试一次，直到 expireTime
     *
     * @param callable   需要执行的操作
     * @param key        锁名
     * @param expireTime 等待时间，锁的过期时间，单位<font size=8 color="red">毫秒</font>
     * @return callable 返回的可空数据 Optional<T>
     */
    public <T> Optional<T> lockfor(Callable<T> callable, String key, long expireTime) {
        return lockfor(callable, null, key, expireTime);

    }
}
