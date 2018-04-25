package com.ppdai.codegen.demo.wire.demo.swagger;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ppdai.codegen.demo.wire.demo.swagger.ignore.IgnoreProcessor;
import com.ppdai.codegen.demo.wire.demo.swagger.mustache.JavaClassNameLambda;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.squareup.wire.schema.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author zhangchengxi
 * Date 2018/4/23
 */
public class DefaultGenerator extends AbstractGenerator implements Generator {
    private static final String SCHEMA = "schema";
    private static final String TYPE_INDEX = "typeIndex";
    //    private JavaGenerator javaGenerator;
    private Schema schema;
    //    private Codegen codegenConfig;
    private IgnoreProcessor ignoreProcessor;

    private Codegen codegen;

    private static ImmutableMap<String, Type> buildTypesIndex(Iterable<ProtoFile> protoFiles) {
        Map<String, Type> result = new LinkedHashMap<>();
        for (ProtoFile protoFile : protoFiles) {
            for (Type type : protoFile.types()) {
                index(result, type);
            }
        }
        return ImmutableMap.copyOf(result);
    }

    private static void index(Map<String, Type> typesByName, Type type) {
        typesByName.put(type.type().toString(), type);
        for (Type nested : type.nestedTypes()) {
            index(typesByName, nested);
        }
    }

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

        HashMap<String, Object> data = Maps.newHashMap();
        data.put(SCHEMA, schema);

        generateModels(data);
        generateApis();
        generateSupportingFiles();


        return null;
    }

    private void generateModels(HashMap<String, Object> data) {
        ImmutableMap<String, Type> typesIndex = collectTypesIndex(data);
        collectAdditionProperties(data);


        for (ProtoFile protoFile : schema.protoFiles()) {
            if (ignoreProcessor.ignore(protoFile)) {
                continue;
            }
            for (Type topLevelType : protoFile.types()) {
                collectRelatedTypes(data, topLevelType, typesIndex);
                collectTopLevelType(data, topLevelType);
                try {
                    processTemplateToFile(data, "model.mustache", "raptor-codegen/raptor-codegen-core/target/generated-sources/" + topLevelType.type().enclosingTypeOrPackage().replace(".", "/") + "/" + topLevelType.type().simpleName() + ".java");
                } catch (IOException e) {
                    throw new RuntimeException("Could not process model '" + "'" + ".Please make sure that your schema is correct!", e);
                }
            }
        }


    }

    private void collectAdditionProperties(HashMap<String, Object> data) {
        data.put("javaClassName", new JavaClassNameLambda());
    }

    private void collectTopLevelType(HashMap<String, Object> data, Type topLevelType) {
        data.put("topLevelType", topLevelType);
    }

    private void collectRelatedTypes(HashMap<String, Object> data, Type type, ImmutableMap<String, Type> typesIndex) {
        HashSet<Type> relatedTypes = Sets.newHashSet();
        if (type instanceof MessageType) {
            ImmutableList<Field> fields = ((MessageType) type).fields();
            for (Field field : fields) {
                String typeName = field.type().toString();
                if (typesIndex.keySet().contains(typeName)) {
                    relatedTypes.add(typesIndex.get(typeName));
                }
            }
        }

        data.put("relationTypes", relatedTypes);
    }

    /**
     * 收集 import需要的数据
     * <p>
     * todo 可能这个方法要放在Codegen中
     *
     * @param data 数据信息
     */
    private ImmutableMap<String, Type> collectTypesIndex(HashMap<String, Object> data) {
        ImmutableMap<String, Type> typesIndex = buildTypesIndex(schema.protoFiles());
        data.put(TYPE_INDEX, typesIndex);
        return typesIndex;


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

            writeToFile(adjustedOutputFilename, tmpl.execute(templateData));
            return new File(adjustedOutputFilename);
        }

        return null;
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
