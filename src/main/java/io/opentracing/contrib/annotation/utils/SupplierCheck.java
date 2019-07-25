package io.opentracing.contrib.annotation.utils;

public interface SupplierCheck<T>  {
    T get() throws Exception;
}
