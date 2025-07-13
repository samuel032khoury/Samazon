package com.samazon.application.utils;

import java.lang.reflect.Field;

public class PatchUtil {
    public static <T> void patchNonNullFields(T source, T target) {
        if (source == null || target == null)
            return;

        Class<?> clazz = source.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(source);
                if (value != null) {
                    field.set(target, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to patch field: " + field.getName(), e);
            }
        }
    }
}