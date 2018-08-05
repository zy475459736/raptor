package com.ppdai.framework.raptor.service;

import com.ppdai.framework.raptor.rpc.Caller;
import com.ppdai.framework.raptor.rpc.URL;

import java.lang.reflect.Method;

/**
 * 服务提供
 *   class/interface         service(即使protobuf中的语义，同时也是服务化体系中的服务语义)    provider
 *   method                  rpc
 * 提供具体服务实现类的具体方法（Method 对象），供外部调用者调用(“传递给”endpoint/servlet)。
 *  1)url 2)interface
 * */
public interface Provider<T> extends Caller {
    /**
     * 服务端本身是提供服务供外部调用的，
     * rpc框架存在一个需求：url->实际方法 这样子的一个映射获取的需求
     * */
    Method lookupMethod(String methodName, String parameterType);

    Class<T> getInterface();
    T getImpl();

    URL getServiceUrl();
    void setServiceUrl(URL serviceUrl);

    //lifecycle
    void destroy();
    void init();

}