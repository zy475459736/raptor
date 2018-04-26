package com.ppdai.codegen.demo.wire.demo.swagger.ignore;

import com.squareup.wire.schema.ProtoFile;

import java.io.File;
/**
 * @author zhangchengxi
 * Date 2018/4/23
 */
public class IgnoreProcessor {
    public boolean ignore(ProtoFile protoFile) {
        return protoFile.packageName().contains("google.protobuf");
    }


    public boolean allowsFile(File file) {
        return true;
    }
}
