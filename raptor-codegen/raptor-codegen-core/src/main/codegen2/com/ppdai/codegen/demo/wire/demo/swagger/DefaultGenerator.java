package com.ppdai.codegen.demo.wire.demo.swagger;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.ppdai.codegen.demo.wire.demo.swagger.ignore.IgnoreProcessor;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
//import com.squareup.javapoet.TypeSpec;
//import com.squareup.wire.java.JavaGenerator;
import com.squareup.wire.schema.*;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangchengxi
 * Date 2018/4/23
 */
public class DefaultGenerator extends AbstractGenerator implements Generator {
//    private JavaGenerator javaGenerator;
    private Schema schema;
//    private Codegen codegenConfig;
    private IgnoreProcessor ignoreProcessor;

    private Codegen codegen;


    @Override
    public Generator opts(ClientOptInput opts) {
        this.schema = opts.getSchema();
//        javaGenerator = JavaGenerator.get(schema);
//        this.codegenConfig = opts.getCodegenConfig();
        ignoreProcessor = new IgnoreProcessor();
        codegen = new DefaultCodegen();
        return this;
    }

    @Override
    public List<File> generate() {
        Preconditions.checkNotNull(schema);
//        Preconditions.checkNotNull(codegenConfig);

        generateModels();
        generateApis();
        generateSupportingFiles();


        return null;
    }

    private void generateModels() {
        //收集model
        collectAllRelationTypes();

        //generate

        try {
            processTemplateToFile(null,"model.mustache","raptor-codegen/raptor-codegen-core/target/generated-sources/TestJava.java");
        } catch (IOException e) {
            throw new RuntimeException("Could not process model '" + "'" + ".Please make sure that your schema is correct!", e);

        }

    }

    public File processTemplateToFile(Map<String, Object> templateData, String templateName, String outputFilename) throws IOException {
        String adjustedOutputFilename = outputFilename.replaceAll("//", "/").replace('/', File.separatorChar);
        if (ignoreProcessor.allowsFile(new File(adjustedOutputFilename))) {
            String templateFile = getFullTemplateFile(codegen, templateName);
            String template = readTemplate(templateFile);
            Mustache.Compiler compiler = Mustache.compiler();
            compiler = codegen.processCompiler(compiler);
            Template tmpl = compiler
                    .withLoader(name -> getTemplateReader(getFullTemplateFile(codegen, name + ".mustache")))
                    .defaultValue("")
                    .compile(template);

            writeToFile(adjustedOutputFilename, tmpl.execute(schema.protoFiles().get(0)));
            return new File(adjustedOutputFilename);
        }

        return null;
    }


    private void collectAllRelationTypes() {
        Set<ProtoType> typeSet = collectRootTypes();
        for (ProtoType protoType : typeSet) {
            Type type = schema.getType(protoType);
//            TypeSpec typeSpec = javaGenerator.generateType(type);
//            System.out.println(typeSpec.name);
        }
    }

    private Set<ProtoType> collectRootTypes() {
        Set<ProtoType> typeSet = Sets.newHashSet();
        for (ProtoFile protoFile : schema.protoFiles()) {
            if (!ignoreProcessor.ignore(protoFile)) {
                for (Service service : protoFile.services()) {
                    for (Rpc rpc : service.rpcs()) {
                        typeSet.add(rpc.requestType());
                        typeSet.add(rpc.responseType());
                    }
                }
            }
        }
        return typeSet;
    }

    private void generateApis() {

    }

    private void generateSupportingFiles() {

    }


}
