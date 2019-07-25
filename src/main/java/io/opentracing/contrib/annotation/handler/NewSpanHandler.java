package io.opentracing.contrib.annotation.handler;

import com.google.common.collect.ImmutableMap;
import io.opentracing.contrib.annotation.NewSpan;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.annotation.SpanTag;
import io.opentracing.contrib.annotation.utils.ExceptionUtils;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

@Aspect
@SuppressWarnings("unused")
public class NewSpanHandler {

    @Around("execution(@io.opentracing.contrib.annotation.NewSpan * * (..))")
    public Object newSpanAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Tracer tracer = GlobalTracer.get();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();

        Span span = startSpan(signature);
        resolveParameter(signature, args, span);

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

    private void resolveParameter(MethodSignature signature, Object[] args, Span span) throws Exception {
        Parameter[] parameters = signature.getMethod().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getType().isAssignableFrom(Span.class)) {
                args[i] = span;
            } else if (parameters[i].getAnnotation(SpanTag.class) != null) {
                setupTag(parameters[i], args[i], span);
            }
        }
    }

    private void setupTag(Parameter parameter, Object arg, Span span) throws Exception {
        SpanTag annotation = parameter.getAnnotation(SpanTag.class);
        String tagKey = annotation.value();
        ExceptionUtils.safeCheckEx( () -> MethodUtils.invokeExactMethod(span, "setTag", tagKey, arg));
    }

    private Span startSpan(MethodSignature signature) {
        Tracer tracer = GlobalTracer.get();
        Span parentSpan = tracer.scopeManager().activeSpan();
        String operationName = getOperationName(signature);

        return tracer.buildSpan(operationName)
                        .asChildOf(parentSpan)
                        .start();
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
