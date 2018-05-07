package com.ppdai.framework.raptor.spring.client;

import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.refer.client.ApacheHttpClient;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.spring.TestApplication;
import com.ppdai.framework.raptor.spring.annotation.RaptorClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * 测试只接入客户端
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class,
        properties = {"raptor.url.com.ppdai.framework.raptor.proto.Simple=http://localhost:8080",
                "simple2=http://test1.ppdai.com"})
@Import(ClientTest.TestConfig.class)
public class ClientTest {

    @RaptorClient
    private Simple simple1;

    @RaptorClient(url = "${simple2}")
    private Simple simple2;

    @RaptorClient(url = "http://test1.ppdai.com/context")
    private Simple simple3;

    @Test
    public void testInit() {
        Assert.assertNotNull(simple1);
        Assert.assertNotNull(simple2);
        Assert.assertNotNull(simple3);

        Assert.assertNotEquals(simple1, simple2);
        Assert.assertNotEquals(simple2, simple3);
    }


    private static ThreadLocal<String> path = new ThreadLocal<>();

    @Test
    public void testPathConfig() {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        try {
            simple3.sayHello(helloRequest);
        } catch (Exception e) {
            String s = path.get();
            Assert.assertEquals("http://test1.ppdai.com/context/com.ppdai.framework.raptor.proto.Simple/sayHello", s);
        }
    }

    @TestConfiguration
    public static class TestConfig {

        @Bean
        public ApacheHttpClient testApacheHttpClient() {
            ApacheHttpClient apacheHttpClient = new ApacheHttpClient() {
                @Override
                protected HttpResponse doSendRequest(HttpPost httpPost, URL serviceUrl) throws IOException {
                    try {
                        return super.doSendRequest(httpPost, serviceUrl);
                    } finally {
                        path.set(httpPost.getURI().toString());
                    }
                }
            };
            apacheHttpClient.init();
            return apacheHttpClient;
        }
    }
}
