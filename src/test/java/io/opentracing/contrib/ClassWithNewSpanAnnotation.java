package io.opentracing.contrib;

import io.opentracing.contrib.annotation.NewSpan;

public class ClassWithNewSpanAnnotation {

    @NewSpan(operationName = "test")
    public void performSomeLogic() {
        System.out.println("mylogic");
    }
}
