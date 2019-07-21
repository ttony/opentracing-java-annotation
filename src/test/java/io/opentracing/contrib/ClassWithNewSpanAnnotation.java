package io.opentracing.contrib;

import io.opentracing.Span;
import io.opentracing.contrib.annotation.NewSpan;

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
}
