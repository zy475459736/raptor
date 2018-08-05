package com.ppdai.framework.raptor.service;

import com.ppdai.framework.raptor.rpc.*;
import com.ppdai.framework.raptor.util.ReflectUtil;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractProvider<T> implements Provider<T> {

    protected Class<T>            interfaceClass;
    @Getter
    protected T                   serviceInstance;

    @Getter
    protected Map<String, Method> methodMap;

    @Getter
    @Setter
    protected URL serviceUrl;


    public AbstractProvider(Class<T> interfaceClass, T serviceInstance) {
        this.interfaceClass  = interfaceClass;
        this.serviceInstance = serviceInstance;
    }

    //Provider---服务提供
    //故初始化阶段对interface中的方法进行“映射关系的建立”
    @Override
    public void init() {
        initMethodMap(interfaceClass);
    }
    /**
     * 初始化接口的方法
     * <p>
     * 假设方法：
     * String test(String)
     * String test1(String)
     * String test1(String,int)
     * <p>
     * 结果：
     * test(String)方法将生成两个key： test，test(String)，因为test方法没有重载，直接用方法名方便使用
     * test1(String)方法将生成一个key：test1(String)
     * test1(String,int)方法将生成一个key：test1(String,int)
     * <p>
     *
     * @param clazz
     */
    private void initMethodMap(Class<T> clazz) {
        this.methodMap = new ConcurrentHashMap<>();
        /***
         * String test(String)
         * String test1(String)
         * String test1(String,int)
         * */
        Method[] methods = clazz.getMethods();
        Map<String, List<Method>> nameMethodMap = new HashMap<>();
        // Collect类中所有的方法
        for (Method method : methods) {
            nameMethodMap.putIfAbsent(method.getName(), new ArrayList<>());
            List<Method> nameMethods = nameMethodMap.get(method.getName());
            nameMethods.add(method);
        }
        /**
         *  test     test(String)
         *  test1    test1(String)|test1(String int)
         * */


        for (String methodName : nameMethodMap.keySet()) {
            List<Method> nameMethods = nameMethodMap.get(methodName);
            //没有重载方法，即方法名唯一的方法
            if (nameMethods.size() == 1) {
                Method method = nameMethods.get(0);
                this.methodMap.put(methodName, method);
            }
            for (Method method : nameMethods) {
                this.methodMap.put(ReflectUtil.getMethodSignature(method), method);
            }
        }
        /**
         * String               Method
         * ------------------------------------------
         *  test                test(String)
         *  test(String)        test(String)
         *  test1(String)       test1(String)
         *  test1(String int)   test1(String int)
         * */
    }

    @Override
    public void destroy() {
        //if needed, what & how
    }

    @Override
    public Response call(Request request) {
        RpcContext.getContext().setRequest(request);
        RpcContextHelper.traceRequest(request);

        Response response = invoke(request);

        RpcContext.getContext().setResponse(response);
        RpcContextHelper.traceResponse(response);

        return response;
    }

    protected abstract Response invoke(Request request);

    @Override
    public Method lookupMethod(String methodName, String parameterType) {
        if (this.getMethodMap().containsKey(methodName)) {
            return this.getMethodMap().get(methodName);
        }
        String key = ReflectUtil.getMethodSignature(methodName, new String[]{parameterType});
        return this.getMethodMap().get(key);
    }



    @Override
    public T getImpl() {
        return this.serviceInstance;
    }

    @Override
    public Class<T> getInterface() {
        return this.interfaceClass;
    }

}
