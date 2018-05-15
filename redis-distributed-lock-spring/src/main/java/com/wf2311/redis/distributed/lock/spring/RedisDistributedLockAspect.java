package com.wf2311.redis.distributed.lock.spring;

import com.wf2311.redis.distributed.lock.common.DistributedLock;
import com.wf2311.redis.distributed.lock.common.LockAction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @date 2018/5/12 16:13.
 * @see  <a href="https://my.oschina.net/dengfuwei/blog/1600681">参考https://my.oschina.net/dengfuwei/blog/1600681</a>
 */
@Aspect
public class RedisDistributedLockAspect {
    private final Logger logger = LoggerFactory.getLogger(RedisDistributedLockAspect.class);

    @Resource
    private DistributedLock distributedLock;

    private ExpressionParser parser = new SpelExpressionParser();

    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Pointcut("@annotation(com.wf2311.redis.distributed.lock.common.LockAction)")
    private void lockPoint() {
    }

    @Around("lockPoint()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        LockAction action = method.getAnnotation(LockAction.class);
        String key = action.field();
        Object[] args = point.getArgs();
        key = parse(key, action.prefix(), method, args);

        int retryTimes = action.action().equals(LockAction.LockFailAction.CONTINUE) ? action.retryTimes() : 0;
        boolean lock = distributedLock.tryLock(key, action.expireTime(), action.expireTimeUnit(), retryTimes, action.sleepTime(), action.sleepTimeUnit());
        if (!lock) {
            logger.debug("get lock failed : " + key);
            return null;
        }

        //得到锁,执行方法，释放锁
        logger.debug("get lock success : " + key);
        try {
            return point.proceed();
        } catch (Exception e) {
            logger.error("execute locked method occured an exception", e);
        } finally {
            boolean releaseResult = distributedLock.unlock(key);
            logger.debug("release lock : " + key + (releaseResult ? " success" : " failed"));
        }
        return null;
    }


    /**
     * 解析spring EL表达式
     *
     * @param key
     * @param method
     * @param args
     * @return
     */
    private String parse(String key, String prefix, Method method, Object[] args) {
        String[] params = discoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);
        }
        if (prefix == null || prefix.isEmpty()) {
            prefix = "";
        } else {
            String split = ":";
            if (!prefix.endsWith(split)) {
                prefix = prefix + split;
            }
        }
        return prefix + parser.parseExpression(key).getValue(context, String.class);
    }
}
