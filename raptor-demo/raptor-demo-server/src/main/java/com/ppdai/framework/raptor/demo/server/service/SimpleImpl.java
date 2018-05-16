package com.ppdai.framework.raptor.demo.server.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdai.framework.raptor.proto.HelloReply;
import com.ppdai.framework.raptor.proto.HelloRequest;
import com.ppdai.framework.raptor.proto.Result;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.spring.annotation.RaptorService;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;

@RaptorService
public class SimpleImpl implements Simple {

    public static final Logger LOGGER = LoggerFactory.getLogger(SimpleImpl.class);

    @Override
    public HelloReply sayHello(HelloRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String hello = "Hello " + "name: " + request.getName() + ", "
                + "snippets: " + request.getSnippets() + ", "
                + "corpus: " + request.getCorpus() + ", "
                + "cats: " + request.getCats() + ", "
                + "result: " + request.getResult() + ", "
                + "bool: " + request.getTbool() + ", "
                + "bytes: " + request.getTbytes().base64() + ", "
                + "double: " + request.getTdouble() + ", "
                + "fixed32: " + request.getTfixed32() + ", "
                + "fixed64: " + request.getTfixed64() + ", "
                + "float: " + request.getTfloat() + ", "
                + "int32: " + request.getTint32() + ", "
                + "int64: " + request.getTint64() + ", "
                + "sfixed32: " + request.getTsfixed32() + ", "
                + "sfixed64: " + request.getTsfixed64() + ", "
                + "unit32: " + request.getTunit32() + ", "
                + "unit64: " + request.getTunit64() + ", "
                + ". " + RandomUtils.nextInt();

        LOGGER.info("request: {}", hello);

        Result result = new Result();
        result.setUrl("http://www.ppdai.com");
        result.setTitle("test");
        result.setSnippets(Arrays.asList("snippet1", "test"));
        return new HelloReply(hello, HelloRequest.Corpus.PRODUCTS, Collections.singletonList(result), 123);
    }

}
