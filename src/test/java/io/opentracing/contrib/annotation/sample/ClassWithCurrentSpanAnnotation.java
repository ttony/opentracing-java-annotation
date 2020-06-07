package io.opentracing.contrib.annotation.sample;

import io.opentracing.Span;
import io.opentracing.contrib.annotation.CurrentSpan;
import io.opentracing.contrib.annotation.SpanTag;
import io.opentracing.contrib.annotation.SpanTagMapper;
import org.springframework.stereotype.Component;

import javax.jms.Message;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertTrue;

@Component
public class ClassWithCurrentSpanAnnotation {

    @CurrentSpan
    public void withSpanArgs(Span span) {
        assertThat(span, not(nullValue()));
    }

    @CurrentSpan
    public void withEmptyArgs() {
        assertTrue(true);
    }

    @CurrentSpan
    public void withNPEThrown() {
        throw new NullPointerException();
    }

    @CurrentSpan
    public void withExtraLogicOnSpanArgs(Span span) {
        assertThat(span, not(nullValue()));
        span.log("this is event log");
    }

    @CurrentSpan
    public void withTag(@SpanTag("tag-name") String tagValue) {
        assertTrue(true);
    }

    @CurrentSpan
    public void withUnsupportedValueTag(@SpanTag("tag-name") byte[] tagValue) {
    }

    @CurrentSpan(tagMapper = @SpanTagMapper(resolver = JmsHeaderTagMapper.class))
    public void withAdvanceTag(Message message) {
        assertTrue(true);
    }

    @CurrentSpan(tagMapper = @SpanTagMapper(resolver = JmsHeaderTagMapper.class))
    public void withAdvanceTagNotMatchedArgument(String message) {
        assertTrue(true);
    }
}
