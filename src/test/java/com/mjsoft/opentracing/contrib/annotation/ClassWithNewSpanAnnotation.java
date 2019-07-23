package com.mjsoft.opentracing.contrib.annotation;

import io.opentracing.Span;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
    public void withExtraLogicOnSpanArgs(Span span) {
        assertThat(span, not(nullValue()));
        span.log("this is event log");
    }

    @NewSpan(operationName="newName")
    public void withOperationName() {}

    @NewSpan
    public void withTag(@SpanTag("tag-name") String tagValue) {
        assertTrue(true);
    }


    public void internalMethodCallwithTag() {
        internalWithTag("another-tag-value");
    }

    @NewSpan
    private void internalWithTag(@SpanTag("tag-name") String tagValue) {
        assertTrue(true);
    }
}
