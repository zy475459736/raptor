package com.ppdai.framework.raptor.spring.server;

import com.ppdai.framework.raptor.proto.HelloReply;
import com.ppdai.framework.raptor.proto.HelloRequest;
import com.ppdai.framework.raptor.proto.MoreService;
import com.ppdai.framework.raptor.proto.Result;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author yinzuolong
 */
@RestController
public class MoreServiceImpl implements MoreService {

    private HelloReply getTestReply() {

        HelloReply reply = new HelloReply();
        reply.setMessage("Hello");

        Result result = new Result();
        result.setUrl("http://www.ppdai.com");
        result.setTitle("test title");
        result.setSnippets(Arrays.asList("snippet1", "test"));

        reply.setResults(Collections.singletonList(result));
        return reply;
    }

    @Override
    public HelloReply testGet1(HelloRequest request, String p1) {
        HelloReply reply = getTestReply();
        reply.setMessage("Hello " + request.getName());
        reply.getResults().get(0).setTitle(p1);
        reply.getResults().get(0).setSnippets(request.getSnippets());

        return reply;
    }

    @Override
    public HelloReply testGet2(HelloRequest request, String p1) {
        HelloReply reply = getTestReply();
        reply.setMessage("Hello " + request.getName());
        reply.setCorpus(request.getCorpus());
        reply.getResults().get(0).setTitle(p1);
        reply.getResults().get(0).setSnippets(request.getSnippets());

        return reply;
    }

    @Override
    public HelloReply testPost1(HelloRequest request, String p1) {
        HelloReply reply = getTestReply();
        reply.setMessage("Hello " + request.getName());
        reply.setCorpus(request.getCorpus());
        reply.getResults().get(0).setTitle(p1);
        reply.getResults().get(0).setSnippets(request.getSnippets());

        return reply;
    }

    @Override
    public HelloReply testPost2(HelloRequest request, String p3, String p1, int p2) {
        HelloReply reply = getTestReply();
        reply.setMessage("Hello " + request.getName());
        reply.setCorpus(request.getCorpus());
        reply.getResults().get(0).setTitle(p1);
        reply.setCode(p2);
        reply.getResults().get(0).setSnippets(request.getSnippets());
        return reply;
    }

    @Override
    public HelloReply testPut1(HelloRequest request, String p1) {
        HelloReply reply = getTestReply();
        reply.setMessage("Hello " + request.getName());
        reply.setCorpus(request.getCorpus());
        reply.getResults().get(0).setTitle(p1);
        reply.getResults().get(0).setSnippets(request.getSnippets());

        return reply;
    }

    @Override
    public HelloReply testDelete1(HelloRequest request, int p2) {
        HelloReply reply = getTestReply();
        reply.setMessage("Hello " + request.getName());
        reply.setCorpus(request.getCorpus());
        reply.getResults().get(0).setSnippets(request.getSnippets());
        reply.setCode(p2);

        return reply;
    }
}
