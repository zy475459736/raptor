package com.ppdai.raptor.codegen2.java.util;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

public class JavaCodeGenPoetHelper {
    Map<FieldSpec, FieldExtendSpec> fields = new HashMap<FieldSpec, FieldExtendSpec>();

    public void addFiledWithGetterAndSetter(FieldSpec fieldSpec, FieldExtendSpec fieldExtendSpec) {
        fields.put(fieldSpec, fieldExtendSpec);
    }

    public JavaFile buildJavaFile(String packageName, String clasName) {
        JavaFile javaFile = JavaFile.builder(packageName, TypeSpec.classBuilder("Clazz").build()).build();
        return javaFile;
    }
}
