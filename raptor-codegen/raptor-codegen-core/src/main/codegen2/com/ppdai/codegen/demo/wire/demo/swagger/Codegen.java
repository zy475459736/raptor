package com.ppdai.codegen.demo.wire.demo.swagger;

import com.samskivert.mustache.Mustache; /**
 * @author zhangchengxi
 * Date 2018/4/23
 */
public interface Codegen {
    Mustache.Compiler processCompiler(Mustache.Compiler compiler);

    String getLibrary();

    String embeddedTemplateDir();

    String templateDir();
}
