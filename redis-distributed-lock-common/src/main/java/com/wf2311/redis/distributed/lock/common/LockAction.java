package com.wf2311.redis.distributed.lock.common;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @date 2018/5/12 15:39.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LockAction {


    /**
     * 锁的资源前缀
     */
    String prefix() default "";

    /**
     * 锁的资源
     */
    String field() default "";

    /**
     * 过期时间
     */
    long expireTime() default DistributedLock.DEFAULT_TIMEOUT;

    /**
     * 过期时间单位
     */
    TimeUnit expireTimeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 获取失败尝试次数
     */
    int retryTimes() default DistributedLock.DEFAULT_RETRY_TIMES;

    /**
     * 休眠时间
     */
    long sleepTime() default DistributedLock.DEFAULT_SLEEP_TIME;

    /**
     * 休眠时间单位
     */
    TimeUnit sleepTimeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 当获取失败时候动作
     */
    LockFailAction action() default LockFailAction.CONTINUE;

    public enum LockFailAction {
        /**
         * 放弃
         */
        GIVEUP,
        /**
         * 结束
         */
        CONTINUE
    }
}
