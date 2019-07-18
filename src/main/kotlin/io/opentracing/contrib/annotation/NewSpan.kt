package io.opentracing.contrib.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class NewSpan(val operationName: String)