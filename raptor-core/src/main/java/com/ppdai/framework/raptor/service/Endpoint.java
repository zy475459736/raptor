package com.ppdai.framework.raptor.service;

import com.ppdai.framework.raptor.rpc.URL;

import java.util.Map;

/**
 * 维护多个provider，负责将provider(服务实现类的具体方法) export给 Servlet 。
 * 默认实现:endPoint同时作为Servlet，以暴露provider。
 * */
public interface Endpoint {

    Map<String, Provider<?>> getProviders();

    URL export(Provider<?> provider, URL serviceUrl);

    URL export(Provider<?> provider);

}
