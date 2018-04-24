package com.ppdai.codegen.demo.wire.demo.swagger.mustache;

import com.google.common.collect.ImmutableMap;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.wire.schema.ProtoType;
import okio.ByteString;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;

import static com.squareup.wire.schema.Options.*;

/**
 * @author zhangchengxi
 * Date 2018/4/24
 */
public class JavaClassNameLambda implements Mustache.Lambda {
    public static final Map<ProtoType, ClassName> classNameMap;

    static {
        // TODO: 2018/4/24 这里引入了 wirepoet依赖,后期可能会去掉
        classNameMap = ImmutableMap.<ProtoType, ClassName>builder()
                .put(ProtoType.BOOL, (ClassName) TypeName.BOOLEAN.box())
                .put(ProtoType.BYTES, ClassName.get(ByteString.class))
                .put(ProtoType.DOUBLE, (ClassName) TypeName.DOUBLE.box())
                .put(ProtoType.FLOAT, (ClassName) TypeName.FLOAT.box())
                .put(ProtoType.FIXED32, (ClassName) TypeName.INT.box())
                .put(ProtoType.FIXED64, (ClassName) TypeName.LONG.box())
                .put(ProtoType.INT32, (ClassName) TypeName.INT.box())
                .put(ProtoType.INT64, (ClassName) TypeName.LONG.box())
                .put(ProtoType.SFIXED32, (ClassName) TypeName.INT.box())
                .put(ProtoType.SFIXED64, (ClassName) TypeName.LONG.box())
                .put(ProtoType.SINT32, (ClassName) TypeName.INT.box())
                .put(ProtoType.SINT64, (ClassName) TypeName.LONG.box())
                .put(ProtoType.STRING, ClassName.get(String.class))
                .put(ProtoType.UINT32, (ClassName) TypeName.INT.box())
                .put(ProtoType.UINT64, (ClassName) TypeName.LONG.box())
                .put(FIELD_OPTIONS, ClassName.get("com.google.protobuf", "MessageOptions"))
                .put(ENUM_OPTIONS, ClassName.get("com.google.protobuf", "FieldOptions"))
                .put(MESSAGE_OPTIONS, ClassName.get("com.google.protobuf", "EnumOptions"))
                .build();
    }


    public JavaClassNameLambda() {
    }


    @Override
    public void execute(Template.Fragment fragment, Writer writer) throws IOException {
        String execute = fragment.execute();
        ProtoType protoType = ProtoType.get(execute);
        ClassName className = classNameMap.get(protoType);
        if(Objects.nonNull(className)){
            String simpleName = className.simpleName();
            writer.write(simpleName);
        }else{
            writer.write(execute);
        }
    }
}
