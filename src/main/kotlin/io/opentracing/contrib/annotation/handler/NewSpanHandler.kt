package io.opentracing.contrib.annotation.handler

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

@Aspect
class NewSpanHandler {

    @Pointcut("@annotation(io.opentracing.contrib.annotation.NewSpan)")
    fun isAnnotated() {}

    @Around("isAnnotated()")
    @Throws(Throwable::class)
    fun newSpanAround(joinPoint: ProceedingJoinPoint): Any {
        println("tont here")
        return joinPoint.proceed()
    }
}