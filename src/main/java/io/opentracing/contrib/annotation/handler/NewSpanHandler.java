package io.opentracing.contrib.annotation.handler;

import com.google.common.collect.ImmutableMap;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.annotation.NewSpan;
import io.opentracing.contrib.annotation.SpanTag;
import io.opentracing.contrib.annotation.utils.ExceptionUtils;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

@Aspect
@Slf4j
@SuppressWarnings("unused")
public class NewSpanHandler {

    private TagMapperHandler tagMapperHandler = new TagMapperHandler();

    @Around("execution(@io.opentracing.contrib.annotation.NewSpan * * (..))")
    public Object newSpanAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Tracer tracer = GlobalTracer.get();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();

        Span span = startSpan(signature);
        Map<String, Object> tags = tagMapperHandler.invokeTagMapper(signature, args);

        setupTag(tags, span);
        resolveParameter(signature, args, span);

        try (Scope scope = tracer.scopeManager().activate(span)) {
            return joinPoint.proceed(args);
        } catch (Throwable ex) {
            Tags.ERROR.set(span, true);
            span.log(ImmutableMap.of(
                    Fields.ERROR_KIND, "Exception",
                    Fields.ERROR_OBJECT, ex));
            throw ex;
        } finally {
            span.finish();
        }
    }

    private void resolveParameter(MethodSignature signature, Object[] args, Span span) {
        Parameter[] parameters = signature.getMethod().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getType().isAssignableFrom(Span.class)) {
                args[i] = span;
            } else if (parameters[i].getAnnotation(SpanTag.class) != null) {
                setupTag(parameters[i], args[i], span);
            }
        }
    }

    private void setupTag(Parameter parameter, Object arg, Span span) {
        SpanTag annotation = parameter.getAnnotation(SpanTag.class);
        String tagKey = annotation.value();
        ExceptionUtils.safeCheckEx( () -> MethodUtils.invokeExactMethod(span, "setTag", tagKey, arg));
    }

    private void setupTag(Map<String, Object> tags, Span span) {
        tags.forEach((k, v) -> ExceptionUtils.safeCheckEx(() -> MethodUtils.invokeExactMethod(span, "setTag", k, v)));
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
        Method method = signature.getMethod();
        NewSpan newSpanAnnotation = method.getAnnotation(NewSpan.class);
        if(StringUtils.isBlank(newSpanAnnotation.operationName())) {
            operationName = ClassUtils.getSimpleName(method.getDeclaringClass()) + "." + signature.getName();
        } else {
            operationName = newSpanAnnotation.operationName();
        }

        return operationName;
    }
}
