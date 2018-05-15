package com.wf2311.redis.distributed.lock.spring.boot.configuration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @date 2018/5/12 17:09.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
@Import({DistributedLockAutoConfiguration.class})
public @interface EnableRedisDistrbutedLock {
}

