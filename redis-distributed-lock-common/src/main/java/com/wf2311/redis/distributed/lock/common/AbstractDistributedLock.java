package com.wf2311.redis.distributed.lock.common;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @date 2018/5/12 15:27.
 */
public abstract class AbstractDistributedLock implements DistributedLock {
    /**
     * 尝试对指定key进行锁操作
     *
     * @param key 锁的资源
     * @return 锁成功返回<code>true</code>;失败返回<code>false</code>
     */
    @Override
    public boolean tryLock(String key) {
        return tryLock(key, DEFAULT_RETRY_TIMES);
    }

    /**
     * 尝试对指定key进行锁操作
     *
     * @param key        锁的资源
     * @param retryTimes 失败后尝试的次数
     * @return 锁成功返回<code>true</code>;失败返回<code>false</code>
     */
    @Override
    public boolean tryLock(String key, int retryTimes) {
        return tryLock(key, retryTimes, DEFAULT_SLEEP_TIME, DEFAULT_TIME_UNIT);
    }

    /**
     * 尝试对指定key进行锁操作
     *
     * @param key           锁的资源
     * @param retryTimes    失败后尝试的次数
     * @param sleepTime     每次失败后的休眠时间
     * @param sleepTimeUnit 休眠时间单位
     * @return 锁成功返回<code>true</code>;失败返回<code>false</code>
     */
    @Override
    public boolean tryLock(String key, int retryTimes, long sleepTime, TimeUnit sleepTimeUnit) {
        return tryLock(key, DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT, retryTimes, sleepTime, sleepTimeUnit);
    }

    /**
     * 尝试对指定key进行锁操作
     *
     * @param key            锁的资源
     * @param expireTime     锁的过期时间
     * @param expireTimeUnit 过期时间单位
     * @return 锁成功返回<code>true</code>;失败返回<code>false</code>
     */
    @Override
    public boolean tryLock(String key, long expireTime, TimeUnit expireTimeUnit) {
        return tryLock(key, expireTime, expireTimeUnit, DEFAULT_RETRY_TIMES, DEFAULT_SLEEP_TIME, DEFAULT_TIME_UNIT);
    }

    /**
     * 尝试对指定key进行锁操作
     *
     * @param key            锁的资源
     * @param expireTime     锁的过期时间
     * @param expireTimeUnit 过期时间单位
     * @param retryTimes     失败后尝试的次数
     * @return 锁成功返回<code>true</code>;失败返回<code>false</code>
     */
    @Override
    public boolean tryLock(String key, long expireTime, TimeUnit expireTimeUnit, int retryTimes) {
        return tryLock(key, expireTime, expireTimeUnit, retryTimes, DEFAULT_SLEEP_TIME, DEFAULT_TIME_UNIT);
    }

    /**
     * 尝试对指定key进行锁操作
     *
     * @param key            锁的资源
     * @param expireTime     锁的过期时间
     * @param expireTimeUnit 过期时间单位
     * @param retryTimes     失败后尝试的次数
     * @param sleepTime      每次失败后的休眠时间
     * @param sleepTimeUnit  休眠时间单位
     * @return
     */
    @Override
    public abstract boolean tryLock(String key, long expireTime, TimeUnit expireTimeUnit, int retryTimes, long sleepTime, TimeUnit sleepTimeUnit);

    /**
     * 尝试对指定key进行锁操作
     *
     * @param key        锁的资源
     * @param expireTime 锁的过期时间
     * @param retryTimes 失败后尝试的次数
     * @param sleepTime  每次失败后的休眠时间
     * @param timeUnit   时间单位
     * @returnt
     */
    @Override
    public boolean tryLock(String key, long expireTime, int retryTimes, long sleepTime, TimeUnit timeUnit) {
        return tryLock(key, expireTime, DEFAULT_TIME_UNIT, retryTimes, sleepTime, DEFAULT_TIME_UNIT);
    }

    /**
     * 解锁
     *
     * @param key 锁的资源
     * @return 解锁成功返回<code>true</code>;失败返回<code>false</code>
     */
    @Override
    public abstract boolean unlock(String key);
}
