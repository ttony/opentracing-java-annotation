package io.opentracing.contrib;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.contrib.annotation.NewSpan;

public class ClassWithNewSpanAnnotation {

    @NewSpan
    public void performSomeLogic(Span span) {
        System.out.println("span :: " + span);
        System.out.println("mylogic");
    }
}
