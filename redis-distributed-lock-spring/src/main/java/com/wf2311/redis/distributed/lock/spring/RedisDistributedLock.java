package com.wf2311.redis.distributed.lock.spring;

import com.wf2311.redis.distributed.lock.common.AbstractDistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @date 2018/5/12 15:55.
 */
public class RedisDistributedLock extends AbstractDistributedLock {
    private final Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);

    private RedisTemplate<Object, Object> redisTemplate;

    private ThreadLocal<String> lockCache = ThreadLocal.withInitial(() -> null);

    private static final String UNLOCK_LUA;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    public RedisDistributedLock(RedisTemplate<Object, Object> redisTemplate) {
        super();
        this.redisTemplate = redisTemplate;
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
    public boolean tryLock(String key, long expireTime, TimeUnit expireTimeUnit, int retryTimes, long sleepTime, TimeUnit sleepTimeUnit) {
        boolean result = setRedis(key, expireTime, expireTimeUnit);
        // 如果获取锁失败，按照传入的重试次数进行重试
        while ((!result) && retryTimes-- > 0) {
            try {
                logger.debug("lock failed, retrying..." + retryTimes);
                sleepTimeUnit.sleep(sleepTime);
            } catch (InterruptedException e) {
                return false;
            }
            result = setRedis(key, expireTime, expireTimeUnit);
        }
        return result;
    }

    /**
     * 解锁
     *
     * @param key 锁的资源
     * @return 解锁成功返回<code>true</code>;失败返回<code>false</code>
     */
    @Override
    public boolean unlock(String key) {
// 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
        try {
            List<String> keys = new ArrayList<String>();
            keys.add(key);
            List<String> args = new ArrayList<String>();
            args.add(lockCache.get());

            // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
            // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本

            Long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
                Object nativeConnection = connection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }

                // 单机模式
                else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }
                return 0L;
            });

            return result > 0;
        } catch (Exception e) {
            logger.error("release lock occured an exception", e);
        } finally {
            // 清除掉ThreadLocal中的数据，避免内存溢出
            lockCache.remove();
        }
        return false;
    }

    private boolean setRedis(String key, long expireTime, TimeUnit unit) {
        try {
            String result = redisTemplate.execute((RedisCallback<String>) connection -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                String uuid = UUID.randomUUID().toString();
                lockCache.set(uuid);
                return commands.set(key, uuid, "NX", "PX", unit.toMillis(expireTime));
            });
            return !StringUtils.isEmpty(result);
        } catch (Exception e) {
            logger.error("set redis occured an exception", e);
        }
        return false;
    }
}
