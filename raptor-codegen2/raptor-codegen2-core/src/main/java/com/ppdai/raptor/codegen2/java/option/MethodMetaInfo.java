package com.ppdai.raptor.codegen2.java.option;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ppdai.framework.raptor.annotation.RaptorMethod;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.wire.schema.Options;
import com.squareup.wire.schema.ProtoMember;
import com.squareup.wire.schema.Rpc;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
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
    private static final String PARAMS_STR = "pathParam";
    private static final String PATH_PARAMS_STR = "requestParam";
    private static final ProtoMember PATH = ProtoMember.get(METHOD_OPTIONS, PATH_STR);
    private static final ProtoMember METHOD = ProtoMember.get(METHOD_OPTIONS, METHOD_STR);
    private static final ProtoMember REQUEST_PARAMS = ProtoMember.get(METHOD_OPTIONS, PARAMS_STR);
    private static final ProtoMember PATH_PARAMS = ProtoMember.get(METHOD_OPTIONS, PATH_PARAMS_STR);

    private static Pattern PATH_PARAM_PATTERN = Pattern.compile("\\{(.*?)}");


    private String path;
    private List<PathParam> pathParams;
    private Method method;
    private Map<String, String> requestParams;
    private String summary;

    public static MethodMetaInfo readFrom(Rpc rpc) {
        Options options = rpc.options();

        String path = OptionUtil.readStringOption(options, PATH);
        String method = OptionUtil.readStringOption(options, METHOD);
        List<String> paramTypesStr = OptionUtil.readStringList(options, REQUEST_PARAMS);
        String summary = OptionUtil.readSummary(rpc.documentation());
        List<PathParam> pathParams = buildPathParams(paramTypesStr);
        List<String> requestParamsStr = OptionUtil.readStringList(options, PATH_PARAMS);
        Map<String, String> requestParams = buildRequestParams(requestParamsStr);


        return MethodMetaInfo.builder()
                .path(path)
                .method(Method.get(method))
                .pathParams(pathParams)
                .requestParams(requestParams)
                .summary(summary)
                .build();
    }

    private static Map<String, String> buildRequestParams(List<String> requestParamsStr) {
        if (CollectionUtils.isEmpty(requestParamsStr)) {
            return Maps.newHashMap();
        }

        List<String> requestParamPairs
                = requestParamsStr.stream()
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        HashMap<String, String> requestParams = Maps.newHashMap();
        for (String requestParamPair : requestParamPairs) {
            int location = requestParamPair.indexOf("=");
            if (location <= 0 || location >= requestParamPair.length() - 1) {
                throw new RuntimeException("illegal requestParams :" + requestParamsStr);
            }
            requestParams.put(requestParamPair.substring(0, location), requestParamPair.substring(location + 1));

        }
        return requestParams;

    }


    private static List<PathParam> buildPathParams(List<String> paramParams) {

        List<String> types;
        if (CollectionUtils.isEmpty(paramParams)) {
            types = Lists.newArrayList();
        } else {
            types = paramParams.stream()
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        ImmutableList.Builder<PathParam> builder = ImmutableList.builder();
        for (String type : types) {
            int location = type.indexOf("=");
            if (location <= 0 || location >= type.length() - 1) {
                throw new RuntimeException("illegal requestParams :" + type);
            }
            PathParam pathParam = new PathParam(type.substring(0, location), type.substring(location + 1));
            builder.add(pathParam);
        }
        return builder.build();
    }

    public AnnotationSpec generateRaptorMethod() {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(RaptorMethod.class);
        OptionUtil.setAnnotationMember(builder, "summary", "$S", summary);
        return builder.build();

    }

}
