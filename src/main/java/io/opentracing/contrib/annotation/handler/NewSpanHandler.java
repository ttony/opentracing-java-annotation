package io.opentracing.contrib.annotation.handler;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@SuppressWarnings("unused")
public class NewSpanHandler {

    @Around("execution(@io.opentracing.contrib.annotation.NewSpan * * (..))")
    public Object newSpanAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("start here");
        Object result = joinPoint.proceed();
        System.out.println("done here");

        return result;
    }
}
