package com.ppdai.codegen.demo.wire.demo.swagger;

import com.squareup.wire.schema.Schema;

/**
 * @author zhangchengxi
 * Date 2018/4/23
 */
public class ClientOptInput {
    private Schema schema;
    private ClientOpts clientOpts;
    private Codegen codegenConfig;

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public ClientOpts getClientOpts() {
        return clientOpts;
    }

    public void setClientOpts(ClientOpts clientOpts) {
        this.clientOpts = clientOpts;
    }

    public Codegen getCodegenConfig() {
        return codegenConfig;
    }

    public void setCodegenConfig(Codegen codegenConfig) {
        this.codegenConfig = codegenConfig;
    }

}
