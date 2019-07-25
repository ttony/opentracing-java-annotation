package io.opentracing.contrib.annotation.utils;


import java.util.function.Supplier;

public class ExceptionUtils {

    public static <T> T safeEx(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            return null;
        }
    }

    public static <T> T safeCheckEx(SupplierCheck<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }
}
