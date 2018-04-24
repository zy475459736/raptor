package com.ppdai.codegen.demo.wire.demo.swagger;

import com.ppdai.codegen.demo.wire.demo.swagger.language.java.AbstractJavaCodegen;
import com.samskivert.mustache.Mustache;

/**
 * @author zhangchengxi
 * Date 2018/4/24
 */
public class DefaultCodegen extends AbstractJavaCodegen{


    @Override
    public Mustache.Compiler processCompiler(Mustache.Compiler compiler) {
        return compiler;
    }

    @Override
    public String getLibrary() {
        return "Java";
    }

    @Override
    public String embeddedTemplateDir() {
        return "Java";
    }

    @Override
    public String templateDir() {
        return "Java";
    }

}
