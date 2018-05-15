package com.wf2311.redis.distributed.lock.spring.boot.configuration;

import com.wf2311.redis.distributed.lock.common.DistributedLock;
import com.wf2311.redis.distributed.lock.spring.RedisDistributedLock;
import com.wf2311.redis.distributed.lock.spring.RedisDistributedLockAspect;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @date 2018/5/12 17:09.
 */
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnClass(DistributedLock.class)
public class DistributedLockAutoConfiguration {

    @Bean
    public DistributedLock redisDistributedLock(RedisTemplate<Object, Object> redisTemplate){
        return new RedisDistributedLock(redisTemplate);
    }

    @Bean
    public RedisDistributedLockAspect lockAspect() {
        return new RedisDistributedLockAspect();
    }


}
