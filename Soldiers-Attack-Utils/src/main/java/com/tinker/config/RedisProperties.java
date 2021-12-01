package com.tinker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Description: RedisProperties
 * <br/>
 * RedisProperties
 *
 * @author laiql
 * @date 2021/3/23 11:01 上午
 */
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    /**
     * Database index used by the connection factory.
     */
    private int database = 0;

    /**
     * Redis server host.
     */
    private String host = "localhost";

    /**
     * Redis server port.
     */
    private int port = 6379;

    /**
     * Login password of the redis server.
     */
    private String password;

    /**
     * Whether to enable SSL support.
     */
    private boolean ssl;

    /**
     * Maximum number of "idle" connections in the pool. Use a negative value to
     * indicate an unlimited number of idle connections.
     */
    private int maxIdle = 8;

    /**
     * Target for the minimum number of idle connections to maintain in the pool. This
     * setting only has an effect if both it and time between eviction runs are
     * positive.
     */
    private int minIdle = 0;

    /**
     * Maximum number of connections that can be allocated by the pool at a given
     * time. Use a negative value for no limit.
     */
    private int maxActive = 8;

    /**
     * Maximum amount of time a connection allocation should block before throwing an
     * exception when the pool is exhausted. Use a negative value to block
     * indefinitely.
     */
    private long maxWait = Duration.ofMillis(-1).toMillis();

    /**
     * Connection time out
     */
    private long timeOut;

    /**
     * Shutdown time out
     */
    private long shutdownTimeOut = Duration.ofMillis(100).toMillis();

    /**
     * Expire time
     */
    private int expire = 30;

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public long getShutdownTimeOut() {
        return shutdownTimeOut;
    }

    public void setShutdownTimeOut(long shutdownTimeOut) {
        this.shutdownTimeOut = shutdownTimeOut;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}
