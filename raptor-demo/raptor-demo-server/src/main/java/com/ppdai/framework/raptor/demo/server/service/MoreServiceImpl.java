package com.ppdai.framework.raptor.demo.server.service;

import com.ppdai.framework.raptor.proto.HelloReply;
import com.ppdai.framework.raptor.proto.HelloRequest;
import com.ppdai.framework.raptor.proto.MoreService;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yinzuolong
 */
@RestController
public class MoreServiceImpl implements MoreService {
    @Override
    public HelloReply testGet1(HelloRequest request, String p1, String p2) {
        Map<String, String> result = new HashMap<>();
        result.put("p1", p1);
        result.put("p2", p2);
        return new HelloReply("testGet1", 123, request, result);
    }

    @Override
    public HelloReply testGet2(HelloRequest request, String p1) {
        Map<String, String> result = new HashMap<>();
        result.put("p1", p1);
        return new HelloReply("testGet2", 123, request, result);
    }

    @Override
    public HelloReply testPost1(HelloRequest request, String p1) {
        Map<String, String> result = new HashMap<>();
        result.put("p1", p1);
        return new HelloReply("testPost1", 123, request, result);
    }

    @Override
    public HelloReply testPost2(HelloRequest request, String p3, String p1, int p2) {
        Map<String, String> result = new HashMap<>();
        result.put("p1", p1);
        result.put("p2", String.valueOf(p2));
        result.put("p3", p3);
        return new HelloReply("testPost2", 123, request, result);
    }

    @Override
    public HelloReply testPut1(HelloRequest request, String p1) {
        Map<String, String> result = new HashMap<>();
        result.put("p1", p1);
        return new HelloReply("testPut1", 123, request, result);
    }

    @Override
    public HelloReply testDelete1(HelloRequest request, int p2) {
        Map<String, String> result = new HashMap<>();
        result.put("p2", String.valueOf(p2));
        return new HelloReply("testDelete1", 123, request, result);
    }
}
