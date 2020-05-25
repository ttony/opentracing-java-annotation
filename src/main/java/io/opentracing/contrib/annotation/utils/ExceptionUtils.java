package io.opentracing.contrib.annotation.utils;


public class ExceptionUtils {

    public static <T> T safeCheckEx(SupplierCheck<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }
}
