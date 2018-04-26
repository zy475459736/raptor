package com.ppdai.framework.raptor.proto;

import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author yinzuolong
 */
public class SimpleExtensionImpl implements SimpleExtension {

    @Override
    public AllTypesPojo testGet1(AllTypesPojo request) {
        return request;
    }

    @Override
    public AllTypesPojo testGet2(AllTypesPojo request, @PathVariable("p1") String p1) {
        return request;
    }

    @Override
    public AllTypesPojo testPost1(AllTypesPojo request) {
        return request;
    }

    @Override
    public AllTypesPojo testPost2(AllTypesPojo request, @PathVariable("p1") String p1, @PathVariable("p2") int p2) {
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
