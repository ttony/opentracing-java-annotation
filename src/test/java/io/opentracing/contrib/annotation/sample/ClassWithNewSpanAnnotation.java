package io.opentracing.contrib.annotation.sample;

import io.opentracing.Span;
import io.opentracing.contrib.annotation.NewSpan;
import io.opentracing.contrib.annotation.SpanTag;
import io.opentracing.contrib.annotation.SpanTagMapper;
import org.springframework.stereotype.Component;

import javax.jms.Message;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertTrue;

@Component
public class ClassWithNewSpanAnnotation {

    @NewSpan
    public void withSpanArgs(Span span) {
        assertThat(span, not(nullValue()));
    }

    @NewSpan
    public void withEmptyArgs() {
        assertTrue(true);
    }

    @NewSpan
    public void withNPEThrown() {
        throw new NullPointerException();
    }

    @NewSpan
    public void withExtraLogicOnSpanArgs(Span span) {
        assertThat(span, not(nullValue()));
        span.log("this is event log");
    }

    @NewSpan(operationName = "newName")
    public void withOperationName() {
    }

    @NewSpan
    public void withTag(@SpanTag("tag-name") String tagValue) {
        assertTrue(true);
    }

    @NewSpan
    public void withUnsupportedValueTag(@SpanTag("tag-name") byte[] tagValue) {}

    @NewSpan(tagMapper = @SpanTagMapper(resolver = JmsHeaderTagMapper.class))
    public void withAdvanceTag(Message message) {
        assertTrue(true);
    }

    @NewSpan(tagMapper = @SpanTagMapper(resolver = JmsHeaderTagMapper.class))
    public void withAdvanceTagNotMatchedArgument(String message) {
        assertTrue(true);
    }
}
