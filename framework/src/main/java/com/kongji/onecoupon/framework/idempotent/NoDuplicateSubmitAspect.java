package com.kongji.onecoupon.framework.idempotent;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.kongji.onecoupon.framework.exception.ClientException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@RequiredArgsConstructor
public class NoDuplicateSubmitAspect {
    private final RedissonClient redissonClient;

    @Around("@annotation(com.kongji.onecoupon.framework.idempotent.NoDuplicateSubmit)")
    public Object noDuplicateSubmit(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取注解
        NoDuplicateSubmit noDuplicateSubmitAnnotation = getNoDuplicateSubmitAnnotation(joinPoint);
        //获取分布式锁标识
        String lockKey=String.format("no-duplicate-submit:path:%s:currentUserId:%s:md:%s",getPath(),getCurrentUserId(),getMD5(joinPoint));
        RLock lock = redissonClient.getLock(lockKey);
        //尝试获取锁
        if(!lock.tryLock()){
            throw new ClientException(noDuplicateSubmitAnnotation.message());
        }
        Object result;
        try {
            //执行业务方法
            result =joinPoint.proceed();
        }finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * @return joinPoint md5
     */
    private String getMD5(ProceedingJoinPoint joinPoint) {
        return DigestUtil.md5Hex(JSON.toJSONBytes(joinPoint.getArgs()));
    }

    /**
     * @return 当前操作用户 ID
     */
    private Object getCurrentUserId() {
        return "1810518709471555585";
    }

    /**
     * @return 获取当前线程上下文 ServletPath
     */
    private Object getPath() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return sra.getRequest().getServletPath();
    }

    /**
     * 获取自定义注解
     * @param joinPoint
     * @return
     */
    private static NoDuplicateSubmit getNoDuplicateSubmitAnnotation(ProceedingJoinPoint joinPoint) {
        //获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取方法
        Method method = signature.getMethod();
        //获取注解返回
        return method.getAnnotation(NoDuplicateSubmit.class);
    }
}
