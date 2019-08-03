package io.opentracing.contrib.annotation.handler;

import com.google.common.collect.Maps;
import io.opentracing.contrib.annotation.NewSpan;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public class TagMapperHandler {

    public Map<String, Object> invokeTagMapper(MethodSignature signature, Object[] args) throws Exception {
        Class tagMapperClass = resolveTagMapperClass(signature);

        if (tagMapperClass == null) return Maps.newHashMap();

        ParameterizedType type = (ParameterizedType) tagMapperClass.getGenericInterfaces()[0];
        Type actualTypeArgument = type.getActualTypeArguments()[0];

        Parameter[] parameters = signature.getMethod().getParameters();
        Object matchedArg = null;
        for (int i=0; i < parameters.length; i++) {
            if (parameters[i].getType().equals(actualTypeArgument)) {
                matchedArg = args[i];
                break;
            }
        }

        if (matchedArg == null) {
            return Maps.newHashMap();
        }

        TagMapper tagMapper = (TagMapper) ConstructorUtils.invokeConstructor(tagMapperClass);
        return tagMapper.tag(matchedArg);
    }

    private Class resolveTagMapperClass(MethodSignature signature) {
        NewSpan newSpan = signature.getMethod().getAnnotation(NewSpan.class);
        if (newSpan.tagMapper().length > 0) {
            Class resolver = newSpan.tagMapper()[0].resolver();

            if (TagMapper.class.isAssignableFrom(resolver)) {
                return resolver;
            }
        }

        return null;
    }
}
