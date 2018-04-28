package com.ppdai.framework.raptor.spring.server;

import com.ppdai.framework.raptor.proto.AllTypesPojo;
import com.ppdai.framework.raptor.proto.SimpleExtension;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yinzuolong
 */
@RestController
public class SimpleExtensionImpl implements SimpleExtension {

    @RequestMapping("/test")
    public String test() {
        return "OK";
    }

    @Override
    public AllTypesPojo testGet1(AllTypesPojo request) {
        return request;
    }

    @Override
    public AllTypesPojo testGet2(AllTypesPojo request, String p1) {
        return request;
    }

    @Override
    public AllTypesPojo testPost1(AllTypesPojo request) {
        return request;
    }

    @Override
    public AllTypesPojo testPost2(AllTypesPojo request,  String p1, @PathVariable("p2") int p2) {
        return request;
    }

    @Override
    public AllTypesPojo testPut1(AllTypesPojo request) {
        return request;
    }

    @Override
    public AllTypesPojo testDelete1(AllTypesPojo request, @PathVariable("p2") int p2) {
        return request;
    }
}
