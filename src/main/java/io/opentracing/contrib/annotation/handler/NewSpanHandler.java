package io.opentracing.contrib.annotation.handler;

import com.google.common.collect.ImmutableMap;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.annotation.NewSpan;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Parameter;

@Aspect
@SuppressWarnings("unused")
public class NewSpanHandler {

    @Around("execution(@io.opentracing.contrib.annotation.NewSpan * * (..))")
    public Object newSpanAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Tracer tracer = GlobalTracer.get();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();

        Span span = tracer.buildSpan(getOperationName(signature)).start();

        Parameter[] parameters = signature.getMethod().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getType().isAssignableFrom(Span.class)) {
                args[i] = span;
            }
        }

        try (Scope scope = tracer.scopeManager().activate(span)) {
            Object result = joinPoint.proceed(args);
            return result;
        } catch (Throwable ex) {
            Tags.ERROR.set(span, true);
            span.log(ImmutableMap.of(Fields.EVENT, "error", Fields.ERROR_OBJECT, ex, Fields.MESSAGE, ex.getMessage()));
            throw ex;
        } finally {
            span.finish();
        }
    }

    private String getOperationName(MethodSignature signature) {
        String operationName;
        NewSpan newSpanAnnotation = signature.getMethod().getAnnotation(NewSpan.class);
        if(StringUtils.isBlank(newSpanAnnotation.operationName())) {
            operationName = signature.getName();
        } else {
            operationName = newSpanAnnotation.operationName();
        }
        return operationName;
    }
}
