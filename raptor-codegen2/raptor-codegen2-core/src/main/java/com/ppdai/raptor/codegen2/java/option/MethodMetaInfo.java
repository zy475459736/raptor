package com.ppdai.raptor.codegen2.java.option;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ppdai.framework.raptor.annotation.RaptorMethod;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.wire.schema.Options;
import com.squareup.wire.schema.ProtoMember;
import com.squareup.wire.schema.Rpc;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.squareup.wire.schema.Options.METHOD_OPTIONS;

/**
 * @author zhangchengxi
 * Date 2018/4/27
 */
@Data
@Builder
public class MethodMetaInfo {
    private static final String PATH_STR = "path";
    private static final String METHOD_STR = "method";
    private static final String PARAMS_STR = "pathParamTypes";
    private static final ProtoMember PATH = ProtoMember.get(METHOD_OPTIONS, PATH_STR);
    private static final ProtoMember METHOD = ProtoMember.get(METHOD_OPTIONS, METHOD_STR);
    private static final ProtoMember PARAM_TYPES = ProtoMember.get(METHOD_OPTIONS, PARAMS_STR);

    private static Pattern PATH_PARAM_PATTERN = Pattern.compile("\\{(.*?)}");


    private String path;
    private List<PathParam> pathParams;
    private Method method;
    private String summary;

    public static MethodMetaInfo readFrom(Rpc rpc) {
        Options options = rpc.options();

        String path = OptionUtil.readStringOption(options, PATH);
        String method = OptionUtil.readStringOption(options, METHOD);
        String paramTypesStr = OptionUtil.readStringOption(options, PARAM_TYPES);
        String summary = OptionUtil.readSummary(rpc.documentation());
        List<PathParam> pathParams = buildPathParams(path, paramTypesStr);

        return MethodMetaInfo.builder()
                .path(path)
                .method(Method.get(method))
                .pathParams(pathParams)
                .summary(summary)
                .build();
    }

    private static List<PathParam> buildPathParams(String path, String paramTypesStr) {
        List<String> pathParamNames = getPathParams(path);

        List<String> types;
        if (Objects.isNull(paramTypesStr)) {
            types = Lists.newArrayList();
        } else {
            types = Arrays.stream(paramTypesStr.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        ImmutableList.Builder<PathParam> builder = ImmutableList.builder();
        if (CollectionUtils.isEmpty(types)) {
            for (String pathParamName : pathParamNames) {
                builder.add(new PathParam(pathParamName, "string"));
            }
        } else if (pathParamNames.size() == types.size()) {
            for (int i = 0; i < pathParamNames.size(); i++) {
                builder.add(new PathParam(pathParamNames.get(i), types.get(i)));
            }
        } else {
            throw new RuntimeException("path param number is not equal to type number,path:" + path + ",type:" + paramTypesStr);
        }

        return builder.build();
    }


    // TODO: 2018/4/27 可以般到一个Util里面
    private static List<String> getPathParams(String path) {
        if(Objects.isNull(path)){
            return Lists.newArrayList();
        }
        Matcher matcher = PATH_PARAM_PATTERN.matcher(path);
        ArrayList<String> result = Lists.newArrayList();
        while (matcher.find()) {
            String group = matcher.group(1);
            result.add(group);
        }
        return result;
    }

    public AnnotationSpec generateRaptorMethod() {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(RaptorMethod.class);
        OptionUtil.setAnnotationMember(builder,"summary", "$S", summary);
        return builder.build();

    }

}
