package com.wf2311.redis.distributed.lock.common;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @date 2018/5/12 14:53.
 */
public interface DistributedLock {

    /**
     * 默认过期时间
     */
    long DEFAULT_TIMEOUT = 30_000;

    TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;

    /**
     * 默认重试次数
     */
    int DEFAULT_RETRY_TIMES = Integer.MAX_VALUE;

    /**
     * 默认休眠时间
     */
    int DEFAULT_SLEEP_TIME = 500;

    /**
     * 尝试对指定key进行锁操作
     *
     * @param key 锁的资源
     * @return 锁成功返回<code>true</code>;失败返回<code>false</code>
     */
    boolean tryLock(String key);

    /**
     * 尝试对指定key进行锁操作
     *
     * @param key        锁的资源
     * @param retryTimes 失败后尝试的次数
     * @return 锁成功返回<code>true</code>;失败返回<code>false</code>
     */
    boolean tryLock(String key, int retryTimes);

    /**
     * 尝试对指定key进行锁操作
     *
     * @param key           锁的资源
     * @param retryTimes    失败后尝试的次数
     * @param sleepTime     每次失败后的休眠时间
     * @param sleepTimeUnit 休眠时间单位
     * @return 锁成功返回<code>true</code>;失败返回<code>false</code>
     */
    boolean tryLock(String key, int retryTimes, long sleepTime, TimeUnit sleepTimeUnit);

    /**
     * 尝试对指定key进行锁操作
     *
     * @param key            锁的资源
     * @param expireTime     锁的过期时间
     * @param expireTimeUnit 过期时间单位
     * @return 锁成功返回<code>true</code>;失败返回<code>false</code>
     */
    boolean tryLock(String key, long expireTime, TimeUnit expireTimeUnit);

    /**
     * 尝试对指定key进行锁操作
     *
     * @param key            锁的资源
     * @param expireTime     锁的过期时间
     * @param expireTimeUnit 过期时间单位
     * @param retryTimes     失败后尝试的次数
     * @return 锁成功返回<code>true</code>;失败返回<code>false</code>
     */
    boolean tryLock(String key, long expireTime, TimeUnit expireTimeUnit, int retryTimes);

    /**
     * 尝试对指定key进行锁操作
     *
     * @param key            锁的资源
     * @param expire         锁的过期时间
     * @param expireTimeUnit 过期时间单位
     * @param retryTimes     失败后尝试的次数
     * @param sleepTime      每次失败后的休眠时间
     * @param sleepTimeUnit  休眠时间单位
     * @return
     */
    boolean tryLock(String key, long expire, TimeUnit expireTimeUnit, int retryTimes, long sleepTime, TimeUnit sleepTimeUnit);

    /**
     * 尝试对指定key进行锁操作
     *
     * @param key        锁的资源
     * @param expire     锁的过期时间
     * @param retryTimes 失败后尝试的次数
     * @param sleepTime  每次失败后的休眠时间
     * @param timeUnit   时间单位
     * @returnt
     */
    boolean tryLock(String key, long expire, int retryTimes, long sleepTime, TimeUnit timeUnit);


    /**
     * 解锁
     *
     * @param key 锁的资源
     * @return 解锁成功返回<code>true</code>;失败返回<code>false</code>
     */
    boolean unlock(String key);

}
