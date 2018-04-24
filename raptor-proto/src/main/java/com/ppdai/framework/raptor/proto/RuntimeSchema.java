package com.ppdai.framework.raptor.proto;

import com.ppdai.framework.raptor.annotation.Protobuf;
import lombok.Getter;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yinzuolong
 */
public class RuntimeSchema<T> {

    private final static Map<String, RuntimeSchema> cache = new ConcurrentHashMap<>();

    @Getter
    private final Map<String, Field> fieldMap = new HashMap<>();

    public static <T> RuntimeSchema<T> getRuntimeSchema(Class<T> typeClass) {
        RuntimeSchema schema = cache.get(typeClass.getName());
        if (schema == null) {
            synchronized (cache) {
                schema = cache.get(typeClass.getName());
                if (schema == null) {
                    schema = createRuntimeSchema(typeClass);
                    cache.put(typeClass.getName(), schema);
                }
            }
        }
        return schema;
    }

    private static <T> RuntimeSchema<T> createRuntimeSchema(Class<T> typeClass) {
        RuntimeSchema<T> schema = new RuntimeSchema<>();
        final Map<String, java.lang.reflect.Field> fieldMap = findInstanceFields(typeClass);
        for (java.lang.reflect.Field f : fieldMap.values()) {
            if (f.getAnnotation(Deprecated.class) != null) {
                continue;
            }
            Protobuf tag = f.getAnnotation(Protobuf.class);
            if (tag == null) {
                String className = typeClass.getCanonicalName();
                String fieldName = f.getName();
                String message = String.format("%s#%s is not annotated with @Protobuf", className, fieldName);
                throw new RuntimeException(message);
            }
            boolean repeated = false;
            if (f.getType().isAssignableFrom(Collection.class)) {
                repeated = true;
            }
            Field field = new Field(tag.order(), tag.fieldType(), f.getType(), f.getName(), repeated);
            schema.fieldMap.put(f.getName(), field);
        }
        return schema;
    }

    private static Map<String, java.lang.reflect.Field> findInstanceFields(
            Class<?> typeClass) {
        LinkedHashMap<String, java.lang.reflect.Field> fieldMap = new LinkedHashMap<String, java.lang.reflect.Field>();
        fill(fieldMap, typeClass);
        return fieldMap;
    }

    private static void fill(Map<String, java.lang.reflect.Field> fieldMap,
                             Class<?> typeClass) {
        if (Object.class != typeClass.getSuperclass()) {
            fill(fieldMap, typeClass.getSuperclass());
        }

        for (java.lang.reflect.Field f : typeClass.getDeclaredFields()) {
            int mod = f.getModifiers();
            if (!Modifier.isStatic(mod) && !Modifier.isTransient(mod)) {
                fieldMap.put(f.getName(), f);
            }
        }
    }
}
