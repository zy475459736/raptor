package com.ppdai.raptor.codegen2.java.option;

import com.google.common.collect.Lists;
import com.squareup.wire.schema.ProtoMember;
import com.squareup.wire.schema.ProtoType;
import com.squareup.wire.schema.Rpc;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.squareup.wire.schema.Options.METHOD_OPTIONS;

/**
 * @author zhangchengxi
 * Date 2018/4/27
 */
@Data
@Builder
public class RequestMapping {
    private static final ProtoMember REQUEST_MAPPING = ProtoMember.get(METHOD_OPTIONS, "requestMapping");
    private static final ProtoType REQUEST_MAPPING_TYPE = ProtoType.get("RequestMapping");
    private static final ProtoMember REQUEST_MAPPING_PATH = ProtoMember.get(REQUEST_MAPPING_TYPE, "path");
    private static final ProtoMember REQUEST_MAPPING_METHOD = ProtoMember.get(REQUEST_MAPPING_TYPE, "method");
    private static final ProtoMember REQUEST_MAPPING_PATH_PARAMS = ProtoMember.get(REQUEST_MAPPING_TYPE, "pathParams");

    private String path;
    private List<PathParam> pathParams = Lists.newArrayList();
    private Method method;

    public static RequestMapping readFrom(Rpc rpc) {
        RequestMappingBuilder builder = RequestMapping.builder();
        Object requestMappingObject = rpc.options().map().get(REQUEST_MAPPING);
        ArrayList<PathParam> pathParams = Lists.newArrayList();

        if (requestMappingObject instanceof Map) {
            Object requestMappingPathObject = ((Map) requestMappingObject).get(REQUEST_MAPPING_PATH);
            if (requestMappingPathObject instanceof String) {
                builder.path((String) requestMappingPathObject);
            }

            Object methodObject = ((Map) requestMappingObject).get(REQUEST_MAPPING_METHOD);
            if (methodObject instanceof String) {
                builder.method(Method.get((String) methodObject));
            }

            Object pathParamsObject = ((Map) requestMappingObject).get(REQUEST_MAPPING_PATH_PARAMS);
            if (pathParamsObject instanceof List) {
                for (Object o : ((List) pathParamsObject)) {
                    if (o instanceof Map) {
                        PathParam pathParam = PathParam.readFrom((Map<ProtoMember, String>) o);
                        pathParams.add(pathParam);
                    }
                }
            }
        }
        builder.pathParams(pathParams);

        return builder.build();

    }
}
