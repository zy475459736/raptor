package com.ppdai.framework.raptor.spring.utils;

import com.ppdai.framework.raptor.annotation.RaptorInterface;
import com.ppdai.framework.raptor.annotation.RaptorMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author yinzuolong
 */
@Slf4j
public class ProtoFileResourceUtils {

    @Data
    public static class ProtoFileInfo {
        private String fileName;
        private String packageName;
        private String content;

        public String getFullName() {
            return StringUtils.isEmpty(packageName) ? fileName : packageName + "." + fileName;
        }
    }

    private static Map<String, ProtoFileInfo> PROTO_FILE_CACHE = new ConcurrentReferenceHashMap<>();

    public static ProtoFileInfo findInterfaceProtoFile(Class<?> raptorInterface, ClassLoader classLoader) {
        if (raptorInterface != null) {
            RaptorInterface annotation = AnnotationUtils.findAnnotation(raptorInterface, RaptorInterface.class);
            if (annotation != null) {
                return findProtoFile(raptorInterface.getPackage().getName(), annotation.protoFile(), classLoader);
            }
        }
        return null;
    }

    public static ProtoFileInfo findMessageProtoFile(Class<?> messageClass, ClassLoader classLoader) {
        RaptorMessage annotation = AnnotationUtils.findAnnotation(messageClass, RaptorMessage.class);
        if (annotation != null) {
            return findProtoFile(messageClass.getPackage().getName(), annotation.protoFile(), classLoader);
        }
        return null;
    }

    public static List<ProtoFileInfo> findProtoFilesByMessage(List<Class<?>> messageClasses, ClassLoader classLoader) {
        Set<ProtoFileInfo> results = new LinkedHashSet<>();
        Set<Class<?>> founds = new HashSet<>();
        for (Class<?> messageClass : messageClasses) {
            Set<ProtoFileInfo> messageProtoFiles = findProtoFilesByMessage(messageClass, founds, classLoader);
            results.addAll(messageProtoFiles);
        }
        return new ArrayList<>(results);
    }

    private static Set<ProtoFileInfo> findProtoFilesByMessage(Class<?> messageClass, Set<Class<?>> founds, ClassLoader classLoader) {
        Set<ProtoFileInfo> results = new HashSet<>();
        ProtoFileInfo protoFileInfo = findMessageProtoFile(messageClass, classLoader);
        if (protoFileInfo != null && !founds.contains(messageClass)) {
            results.add(protoFileInfo);
            founds.add(messageClass);
            Field[] fields = messageClass.getDeclaredFields();
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                RaptorMessage annotation = AnnotationUtils.findAnnotation(fieldType, RaptorMessage.class);
                ResolvableType resolvableType = ResolvableType.forField(field);
                if (annotation != null) {
                    Set<ProtoFileInfo> filedMessageProtoFiles = findProtoFilesByMessage(fieldType, founds, classLoader);
                    results.addAll(filedMessageProtoFiles);
                } else if (List.class.isAssignableFrom(fieldType)) {
                    ResolvableType genericType = resolvableType.getGeneric(0);
                    results.addAll(findProtoFilesByMessage(genericType.getRawClass(), founds, classLoader));
                } else if (Map.class.isAssignableFrom(fieldType)) {
                    ResolvableType keyType = resolvableType.getGeneric(0);
                    ResolvableType valueType = resolvableType.getGeneric(1);
                    results.addAll(findProtoFilesByMessage(keyType.getRawClass(), founds, classLoader));
                    results.addAll(findProtoFilesByMessage(valueType.getRawClass(), founds, classLoader));
                }
            }
        }
        return results;
    }

    public static ProtoFileInfo findProtoFile(String packageName, String fileName, ClassLoader classLoader) {
        fileName = StringUtils.endsWithIgnoreCase(fileName, ".proto") ? fileName : fileName + ".proto";
        String resource = packageName.replace(".", "/") + "/" + fileName;
        if (PROTO_FILE_CACHE.get(resource) != null) {
            return PROTO_FILE_CACHE.get(resource);
        }
        ProtoFileInfo protoFileInfo = new ProtoFileInfo();
        protoFileInfo.setFileName(fileName);
        protoFileInfo.setPackageName(packageName);

        if (classLoader == null) {
            classLoader = ProtoFileResourceUtils.class.getClassLoader();
        }
        try (InputStream in = classLoader.getResourceAsStream(resource)) {
            String content = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
            protoFileInfo.setContent(content);
            PROTO_FILE_CACHE.put(resource, protoFileInfo);
            return protoFileInfo;
        } catch (Exception e) {
            log.error("Read proto file [{}] error.", resource, e);
        }
        return null;
    }
}
