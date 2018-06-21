package com.ppdai.framework.raptor.demo.client;

import com.ppdai.framework.raptor.proto.HelloReply;
import com.ppdai.framework.raptor.proto.HelloRequest;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.spring.annotation.RaptorClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootClient.class)
public class RaptorApacheHttpClientTest {

    @RaptorClient
    private Simple simple;

    @Test
    public void testClient() {
        HelloRequest helloRequest = new HelloRequest();
        helloRequest.setName("ppdai");
        HelloReply reply = simple.sayHello(helloRequest);
        Assert.assertTrue(StringUtils.startsWithIgnoreCase(reply.getMessage(), "Hello"));
    }

    //TODO mockserver
}
