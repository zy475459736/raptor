package com.ppdai.raptor.codegen2.java.option;

import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.wire.schema.Options;
import com.squareup.wire.schema.ProtoMember;
import okio.ByteString;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhangchengxi
 * Date 2018/4/28
 */
public class OptionUtil {
    private static final Map<String, TypeName> BUILD_IN_TYPE_MAP =
            ImmutableMap.<String, TypeName>builder()
                    .put("double", TypeName.DOUBLE)
                    .put("float", TypeName.FLOAT)
                    .put("int64", TypeName.LONG)
                    .put("uint64", TypeName.LONG)
                    .put("int32", TypeName.INT)
                    .put("fixed64", TypeName.LONG)
                    .put("fixed32", TypeName.INT)
                    .put("bool", TypeName.BOOLEAN)
                    .put("string", ClassName.get(String.class))
                    .put("bytes", ClassName.get(ByteString.class))
                    .put("uint32", TypeName.INT)
                    .put("sfixed32", TypeName.INT)
                    .put("sfixed64", TypeName.LONG)
                    .put("sint32", TypeName.INT)
                    .put("sint64", TypeName.LONG)
                    .put("int", TypeName.INT)
                    .put("boolean", TypeName.BOOLEAN)
                    .build();


    private static final Pattern PATTERN = Pattern.compile("@[Ss]ummary(.*)$", Pattern.MULTILINE);

    public static TypeName getJavaType(String type) {
        TypeName typeName = BUILD_IN_TYPE_MAP.get(type);
        if (Objects.isNull(typeName)) {
            throw new RuntimeException("type name not supported: type=" + type);
        }
        return typeName;
    }


    public static String readStringOption(Options options, ProtoMember key) {
        return readStringOption(options, key, null);
    }

    public static String readStringOption(Options options, ProtoMember key, String defaultValue) {
        Object value = options.get(key);
        if (value instanceof String) {
            return (String) value;
        }
        return defaultValue;
    }

    public static String readSummary(String documentation) {
        Matcher matcher = PATTERN.matcher(documentation);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static void main(String[] args) {
        String testText = "line1 \n line2 \n line3 @Summary this is summary\n line4\n";
        System.out.println(readSummary(testText));
    }

    public static AnnotationSpec.Builder setAnnotationMember(AnnotationSpec.Builder builder, String name, String format, Object... args) {
        if (Arrays.stream(args).anyMatch(Objects::nonNull)) {
            builder.addMember(name, format, args);
        }
        return builder;
    }
}
