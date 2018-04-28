package com.ppdai.raptor.codegen2.java.option;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.wire.schema.Options;
import com.squareup.wire.schema.ProtoMember;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhangchengxi
 * Date 2018/4/28
 */
public class OptionUtil {
    public static final String SUMMARY_TOKEN = "@summary";
    private static final Pattern PATTERN = Pattern.compile("@[Ss]ummary(.*)$",Pattern.MULTILINE);

    public static String readStringOption(Options options, ProtoMember key){
        return readStringOption(options,key,null);
    }

    public static String readStringOption(Options options, ProtoMember key,String defaultValue){
        Object value = options.get(key);
        if(value instanceof String){
            return (String) value;
        }
        return defaultValue;
    }

    public static String readSummary(String documentation) {
        Matcher matcher = PATTERN.matcher(documentation);
        if(matcher.find()){
            return matcher.group(0);
        }
        return null;
    }

    public static void main(String[] args) {
        String testText = "line1 \n line2 \n line3 @Summary this is summary\n line4\n";
        System.out.println(readSummary(testText));
    }

    public static AnnotationSpec.Builder setAnnotationMember(AnnotationSpec.Builder builder, String name, String format, Object... args) {
        if(Arrays.stream(args).anyMatch(Objects::nonNull)){
            builder.addMember(name,format,args);
        }
        return builder;
    }
}
