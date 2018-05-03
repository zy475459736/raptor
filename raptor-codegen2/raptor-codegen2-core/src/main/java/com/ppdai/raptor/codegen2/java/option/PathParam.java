package com.ppdai.raptor.codegen2.java.option;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ppdai.raptor.codegen2.java.JavaGenerator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.wire.schema.ProtoMember;
import com.squareup.wire.schema.ProtoType;
import lombok.Builder;
import lombok.Data;
import okio.ByteString;

import java.util.Map;
import java.util.Objects;

/**
 * @author zhangchengxi
 * Date 2018/4/27
 */
@Data
@Builder
public class PathParam {
    private static final ProtoType PATH_PARAM_TYPE = ProtoType.get("PathParam");
    private static final ProtoMember REQUEST_MAPPING_NAME = ProtoMember.get(PATH_PARAM_TYPE, "name");
    private static final ProtoMember REQUEST_MAPPING_TYPE = ProtoMember.get(PATH_PARAM_TYPE, "type");
    private static final Map<String, TypeName> BUILD_IN_TYPE_MAP =
            // TODO: 2018/5/3 补充类型
            ImmutableMap.<String, TypeName>builder()
                    .put("TYPE_DOUBLE", TypeName.DOUBLE)
                    .put("TYPE_FLOAT", TypeName.FLOAT)
                    .put("TYPE_INT64", TypeName.LONG)
                    .put("TYPE_UINT64", TypeName.LONG)
                    .put("TYPE_INT32", TypeName.INT)
                    .put("TYPE_FIXED64", TypeName.LONG)
                    .put("TYPE_FIXED32", TypeName.INT)
                    .put("TYPE_BOOL", TypeName.BOOLEAN)
                    .put("TYPE_STRING", ClassName.get(String.class))
                    .put("TYPE_BYTES", ClassName.get(ByteString.class))
                    .put("TYPE_UINT32", TypeName.INT)
                    .put("TYPE_SFIXED32", TypeName.INT)
                    .put("TYPE_SFIXED64", TypeName.LONG)
                    .put("TYPE_SINT32", TypeName.INT)
                    .put("TYPE_SINT64", TypeName.LONG)
                    .put("int",TypeName.INT)
                    .put("string",ClassName.get(String.class))
                    .build();

    private String name;
    private String type;

    public static PathParam readFrom(Map<ProtoMember, String> map) {
        String name = map.get(REQUEST_MAPPING_NAME);
        String type = map.get(REQUEST_MAPPING_TYPE);
        return new PathParam(name, type);
    }

    public static TypeName getJavaType(String type) {
        TypeName typeName = BUILD_IN_TYPE_MAP.get(type);
        if(Objects.isNull(typeName)){
            throw new RuntimeException("type name not supported: type="+type);
        }
        return typeName;
    }


}
