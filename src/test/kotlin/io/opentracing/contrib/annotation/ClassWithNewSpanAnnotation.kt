package io.opentracing.contrib.annotation

class ClassWithNewSpanAnnotation {

    @NewSpan("mytest")
    fun performSomeLogic() {
        println("mylogic")
    }
}