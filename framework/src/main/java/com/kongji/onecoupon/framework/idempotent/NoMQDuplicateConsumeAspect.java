

package com.kongji.onecoupon.framework.idempotent;

import com.kongji.onecoupon.framework.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 防止消息队列消费者重复消费消息切面控制器
 * <p>
 * 作者：kongji
 *     
 *     
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public final class NoMQDuplicateConsumeAspect {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String LUA_SCRIPT = """
            local key = KEYS[1]
            local value = ARGV[1]
            local expire_time_ms = ARGV[2]
            -- 获取现有值
            local existing_value = redis.call('GET', key)

            -- 如果键不存在，设置新的值并返回 nil
            if not existing_value then
                    redis.call('SET', key, value, 'NX', 'PX', expire_time_ms)
                    return nil
            else
                return existing_value
            end
            """;

    /**
     * 增强方法标记 {@link NoMQDuplicateConsume} 注解逻辑
     */
    @Around("@annotation(com.kongji.onecoupon.framework.idempotent.NoMQDuplicateConsume)")
    public Object noMQRepeatConsume(ProceedingJoinPoint joinPoint) throws Throwable {
        NoMQDuplicateConsume noMQDuplicateConsume = getNoMQDuplicateConsumeAnnotation(joinPoint);
        String uniqueKey = noMQDuplicateConsume.keyPrefix() + SpELUtil.parseKey(noMQDuplicateConsume.key(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());

        String absentAndGet = stringRedisTemplate.execute(
                RedisScript.of(LUA_SCRIPT, String.class),
                List.of(uniqueKey),
                IdempotentMQConsumeStatusEnum.CONSUMING.getCode(),
                String.valueOf(TimeUnit.SECONDS.toMillis(noMQDuplicateConsume.keyTimeout()))
        );

        // 如果不为空证明已经有
        if (Objects.nonNull(absentAndGet)) {
            boolean errorFlag = IdempotentMQConsumeStatusEnum.isError(absentAndGet);
            log.warn("[{}] MQ repeated consumption, {}.", uniqueKey, errorFlag ? "Wait for the client to delay consumption" : "Status is completed");
            if (errorFlag) {
                throw new ServiceException(String.format("消息消费者幂等异常，幂等标识：%s", uniqueKey));
            }
            return null;
        }

        Object result;
        try {
            // 执行标记了消息队列防重复消费注解的方法原逻辑
            result = joinPoint.proceed();

            // 设置防重令牌 Key 过期时间，单位秒
            stringRedisTemplate.opsForValue().set(uniqueKey, IdempotentMQConsumeStatusEnum.CONSUMED.getCode(), noMQDuplicateConsume.keyTimeout(), TimeUnit.SECONDS);
        } catch (Throwable ex) {
            // 删除幂等 Key，让消息队列消费者重试逻辑进行重新消费
            stringRedisTemplate.delete(uniqueKey);
            throw ex;
        }
        return result;
    }

    /**
     * @return 返回自定义防重复消费注解
     */
    public static NoMQDuplicateConsume getNoMQDuplicateConsumeAnnotation(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        return targetMethod.getAnnotation(NoMQDuplicateConsume.class);
    }
}
