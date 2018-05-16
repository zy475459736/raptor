package com.ppdai.framework.raptor.spring.server;

import com.ppdai.framework.raptor.proto.HelloReply;
import com.ppdai.framework.raptor.proto.HelloRequest;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.spring.annotation.RaptorService;
import com.ppdai.framework.raptor.spring.aop.AopAnnotation1;
import com.ppdai.framework.raptor.spring.aop.AopAnnotation2;
import org.apache.commons.lang3.RandomUtils;

@RaptorService
public class SimpleImpl implements Simple {

    @AopAnnotation1
    @AopAnnotation2
    @Override
    public HelloReply sayHello(HelloRequest request) {
        String hello = "Hello " + request.getName() + ". " + RandomUtils.nextInt();
        HelloReply helloReply = new HelloReply();
        helloReply.setMessage(hello);
        return helloReply;
    }

}
