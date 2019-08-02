package io.opentracing.contrib.annotation;

import com.google.common.collect.Maps;

import javax.jms.Message;
import java.util.Map;

public class JmsHeaderTagMapper {

    public Map<String, Object> tag(Message message) {
        return Maps.newHashMap();
    }
}
