package io.opentracing.contrib.annotation.sample;

import com.google.common.collect.Maps;
import io.opentracing.contrib.annotation.handler.TagMapper;

import javax.jms.Message;
import java.util.HashMap;
import java.util.Map;

public class JmsHeaderTagMapper implements TagMapper<Message> {

    public Map<String, Object> tag(Message message) throws Exception {
        HashMap<String, Object> tag = Maps.newHashMap();
        tag.put("msg-id", message.getJMSMessageID());

        return tag;
    }
}
