package io.opentracing.contrib.annotation.handler;

import java.util.Map;

public interface TagMapper<T> {
    Map<String, Object> tag(T parameter) throws Exception;
}
