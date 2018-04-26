package com.ppdai.raptor.codegen2.java.util;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.Map;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

public class JavaCodeGenPoetHelper {
    Map<FieldSpec, FieldExtendSpec> fields = new HashMap<FieldSpec, FieldExtendSpec>();

    public void addFiled(FieldSpec fieldSpec, FieldExtendSpec fieldExtendSpec) {
        fields.put(fieldSpec, fieldExtendSpec);
    }

    public JavaFile buildJavaFile(String packageName, String clasName) {
        TypeSpec.Builder typespecBuilder = TypeSpec.classBuilder(clasName);
        typespecBuilder.addModifiers(PUBLIC, FINAL);

        for (FieldSpec fieldSpec : fields.keySet()) {
            FieldExtendSpec fieldExtendSpec = fields.get(fieldSpec);
            if (fieldExtendSpec.isGenerateGetterAndSetter()) {
                typespecBuilder.addField(fieldSpec);
                MethodSpec getterMethodSpec = MethodSpec.methodBuilder("get" + StringUtils.captureName(fieldSpec.name)).addModifiers(Modifier.PUBLIC)
                        .addParameter(fieldSpec.type, fieldSpec.name, Modifier.FINAL).returns(fieldSpec.type).addStatement("return this." + fieldSpec.name + ";").build();
                typespecBuilder.addMethod(getterMethodSpec);
                MethodSpec setterMethodSpec = MethodSpec.methodBuilder("set" + StringUtils.captureName(fieldSpec.name)).addModifiers(Modifier.PUBLIC)
                        .addParameter(fieldSpec.type, fieldSpec.name, Modifier.FINAL).returns(void.class).addStatement("this." + fieldSpec.name + " = " + fieldSpec.name + ";").build();
                typespecBuilder.addMethod(setterMethodSpec);
            }
        }

        JavaFile javaFile = JavaFile.builder(packageName, typespecBuilder.build()).build();
        return javaFile;
    }

    public static void main(String[] args) {
        JavaCodeGenPoetHelper javaCodeGenPoetHelper = new JavaCodeGenPoetHelper();
        FieldExtendSpec fieldExtendSpec = new FieldExtendSpec();
        fieldExtendSpec.setGenerateGetterAndSetter(true);
        javaCodeGenPoetHelper.addFiled(FieldSpec.builder(int.class, "userCount", Modifier.PRIVATE).build(), fieldExtendSpec);
        JavaFile javaFile = javaCodeGenPoetHelper.buildJavaFile("org.leo.demo.javapoet", "DemoPoetHelper");
        System.out.println(javaFile.toString());
    }
}
