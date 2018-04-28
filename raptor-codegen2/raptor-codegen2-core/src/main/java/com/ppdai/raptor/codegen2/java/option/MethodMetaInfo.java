package com.ppdai.raptor.codegen2.java.option;

import com.google.common.collect.Lists;
import com.squareup.wire.schema.ProtoMember;
import com.squareup.wire.schema.Rpc;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private List<PathParam> pathParams = Lists.newArrayList();
    private Method method;

    // TODO: 2018/4/27 zcx:太丑,重构
    public static MethodMetaInfo readFrom(Rpc rpc) {
        Map<ProtoMember, Object> optionMap = rpc.options().map();


        MethodMetaInfoBuilder builder = MethodMetaInfo.builder();
        List<PathParam> pathParams = Lists.newArrayList();

        Optional<String> pathOptional = Optional.ofNullable(optionMap.get(PATH))
                .filter(String.class::isInstance)
                .map(String.class::cast);

        if (pathOptional.isPresent()) {
            String path = pathOptional.get();
            builder.path(path);

            List<String> pathParamNames = getPathParams(path);

            Optional<String> typeOptional = Optional.ofNullable(optionMap.get(PARAM_TYPES))
                    .filter(String.class::isInstance)
                    .map(String.class::cast);
            if (typeOptional.isPresent()) {
                String types = typeOptional.get();
                String[] split = types.split(",");
                if (split.length == pathParamNames.size()) {
                    for (int i = 0; i < split.length; i++) {
                        pathParams.add(new PathParam(pathParamNames.get(i),split[i]));
                    }
                } else {
                    throw new RuntimeException("path param number is not equal to type number,path:"+path+",type:"+types);
                }
            } else {
                for (String pathParamName : pathParamNames) {
                    pathParams.add(new PathParam(pathParamName,"string"));
                }
            }
        }
        builder.pathParams(pathParams);

        Method method = Optional.ofNullable(optionMap.get(METHOD))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(Method::get)
                .orElse(null);
        builder.method(method);




//        if (requestMappingObject instanceof Map) {
//
//            Object pathParamsObject = ((Map) requestMappingObject).get(REQUEST_MAPPING_PATH_PARAMS);
//            if (pathParamsObject instanceof List) {
//                for (Object o : ((List) pathParamsObject)) {
//                    if (o instanceof Map) {
//                        PathParam pathParam = PathParam.readFrom((Map<ProtoMember, String>) o);
//                        pathParams.add(pathParam);
//                    }
//                }
//            }
//        }
//        builder.pathParams(pathParams);

        return builder.build();

    }


    // TODO: 2018/4/27 可以般到一个Util里面
    private static List<String> getPathParams(String path) {
        Matcher matcher = PATH_PARAM_PATTERN.matcher(path);
        ArrayList<String> result = Lists.newArrayList();
        while (matcher.find()) {
            String group = matcher.group(1);
            result.add(group);
        }
        return result;
    }
}
