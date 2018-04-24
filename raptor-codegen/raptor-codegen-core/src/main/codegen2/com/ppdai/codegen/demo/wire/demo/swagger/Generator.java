package com.ppdai.codegen.demo.wire.demo.swagger;

import java.io.File;
import java.util.List;

/**
 * @author zhangchengxi
 * Date 2018/4/23
 */
public interface Generator {
    Generator opts(ClientOptInput opts);

    List<File> generate();
}
