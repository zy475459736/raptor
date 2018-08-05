package com.ppdai.framework.raptor.refer.client;

import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.serialize.Serialization;

/**
 * 是否职责不单一？
 * */
public interface Client {
    void init();

    void destroy();

    Response sendRequest(Request request, URL serviceUrl);
    //设置编码解码方式
    void setSerialization(Serialization serialization);
}
