package com.ppdai.raptor.codegen2.java.option;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ppdai.framework.raptor.annotation.RaptorInterface;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.wire.schema.Options;
import com.squareup.wire.schema.ProtoFile;
import com.squareup.wire.schema.ProtoMember;
import com.squareup.wire.schema.Service;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


/**
 * @author zhangchengxi
 * Date 2018/4/28
 */
@Data
@Builder
public class InterfaceMetaInfo {
    private static final ProtoMember SERVICE_PATH = ProtoMember.get(Options.SERVICE_OPTIONS, "servicePath");
    private static final ProtoMember APP_ID = ProtoMember.get(Options.FILE_OPTIONS, "appId");
    private static final ProtoMember APP_NAME = ProtoMember.get(Options.FILE_OPTIONS, "appName");
    private static final ProtoMember VERSION = ProtoMember.get(Options.FILE_OPTIONS, "version");

    private String servicePath;
    private String appId;
    private String appName;
    private String version;
    private String protoFile;
    private String crc32;
    private String summary;


    public static InterfaceMetaInfo readFrom(ProtoFile protoFile, Service service) {
        String servicePath = OptionUtil.readStringOption(service.options(), SERVICE_PATH);
        String appId = OptionUtil.readStringOption(protoFile.options(), APP_ID);
        String appName = OptionUtil.readStringOption(protoFile.options(), APP_NAME);
        String version = OptionUtil.readStringOption(protoFile.options(), VERSION);
        String protoFileName = protoFile.name();
        // TODO: 2018/4/28 zhangchengxi
        String crc32 = null;
        String summary = OptionUtil.readSummary(service.documentation());

        InterfaceMetaInfoBuilder builder = InterfaceMetaInfo.builder();
        builder.servicePath(servicePath)
                .appId(appId)
                .appName(appName)
                .version(version)
                .protoFile(protoFileName)
                .crc32(crc32)
                .summary(summary);

        return builder.build();
    }

    public AnnotationSpec generateInterfaceSpec() {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(RaptorInterface.class);
        OptionUtil.setAnnotationMember(builder, "appId", "$S", appId);
        OptionUtil.setAnnotationMember(builder, "appName", "$S", appName);
        OptionUtil.setAnnotationMember(builder, "version", "$S", version);
        OptionUtil.setAnnotationMember(builder, "protoFile", "$S", protoFile);
        OptionUtil.setAnnotationMember(builder, "crc32", "$S", crc32);
        OptionUtil.setAnnotationMember(builder, "summary", "$S", summary);

        return builder.build();

    }

    public AnnotationSpec generateRequestMappingSpec() {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(RequestMapping.class);
         OptionUtil.setAnnotationMember(builder, "path", "$S", servicePath);
         return builder.build();
    }
    public List<AnnotationSpec> generateAnnotations() {
        return ImmutableList.of(generateInterfaceSpec(), generateRequestMappingSpec());
    }

}
