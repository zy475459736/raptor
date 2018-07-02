package com.ppdai.framework.raptor.spring.utils;

import com.ppdai.framework.raptor.annotation.RaptorInterface;
import lombok.Data;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author yinzuolong
 */
public class ProtoFileResourceUtils {

    @Data
    public static class ProtoFileInfo {
        private String appId;
        private String appName;
        private String fileName;
        private String packageName;
        private String content;

        public String getFullName() {
            return StringUtils.isEmpty(packageName) ? fileName : packageName + "." + fileName;
        }
    }

    private static Map<String, String> PROTO_FILE_CACHE = new ConcurrentReferenceHashMap<>();

    public static ProtoFileInfo findProtoFile(Class<?> raptorInterface, ClassLoader classLoader) {
        if (raptorInterface != null) {
            RaptorInterface annotation = AnnotationUtils.findAnnotation(raptorInterface, RaptorInterface.class);
            if (annotation != null) {
                ProtoFileInfo protoFileInfo = new ProtoFileInfo();
                protoFileInfo.setAppId(annotation.appId());
                protoFileInfo.setAppName(annotation.appName());
                protoFileInfo.setFileName(StringUtils.endsWithIgnoreCase(annotation.protoFile(), ".proto") ? annotation.protoFile() : annotation.protoFile() + ".proto");
                protoFileInfo.setPackageName(raptorInterface.getPackage().getName());
                protoFileInfo.setContent(getProtoFileContent(protoFileInfo.packageName, protoFileInfo.fileName, classLoader));
                return protoFileInfo;
            }
        }
        return null;
    }

    public static String getProtoFileContent(String packageName, String fileName, ClassLoader classLoader) {
        String resource = packageName.replace(".", "/") + "/" + fileName;
        if (PROTO_FILE_CACHE.get(resource) != null) {
            return PROTO_FILE_CACHE.get(resource);
        }
        if (classLoader == null) {
            classLoader = ProtoFileResourceUtils.class.getClassLoader();
        }
        try (InputStream in = classLoader.getResourceAsStream(resource)) {
            String content = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
            PROTO_FILE_CACHE.put(resource, content);
            return content;
        } catch (IOException ignored) {
        }
        return null;
    }
}
