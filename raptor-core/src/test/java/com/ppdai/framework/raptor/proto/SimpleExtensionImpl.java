package com.ppdai.framework.raptor.proto;

/**
 * @author yinzuolong
 */
public class SimpleExtensionImpl implements SimpleExtension {

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
    public AllTypesPojo testPost2(AllTypesPojo request, String p1, int p2) {
        return request;
    }

    @Override
    public AllTypesPojo testPut1(AllTypesPojo request) {
        return request;
    }

    @Override
    public AllTypesPojo testDelete1(AllTypesPojo request, int p2) {
        return request;
    }
}
