package com.ppdai.codegen.demo.wire.demo;

import com.ppdai.codegen.demo.wire.demo.swagger.ClientOptInput;
import com.ppdai.codegen.demo.wire.demo.swagger.DefaultGenerator;
import com.squareup.wire.schema.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author zhangchengxi
 * Date 2018/4/23
 */
public class WireSwaggerDemo {

    public static void main(String[] args) throws IOException {
        String dictionary1 = "/Users/zhangchengxi/Desktop/testwire";
        SchemaLoader schemaLoader = new SchemaLoader();
        schemaLoader.addSource(new File(dictionary1));
        Schema schema = schemaLoader.load();

        ProtoFile protoFile = schema.protoFiles().get(0);

        Field field = ((MessageType) protoFile.types().get(0)).fields().get(0);

        ClientOptInput opts = new ClientOptInput();
        opts.setSchema(schema);
        List<File> generate = new DefaultGenerator().opts(opts).generate();


    }
}
