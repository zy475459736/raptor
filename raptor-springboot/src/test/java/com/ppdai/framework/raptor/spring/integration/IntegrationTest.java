package com.ppdai.framework.raptor.spring.integration;

import com.ppdai.framework.raptor.proto.*;
import com.ppdai.framework.raptor.spring.TestApplication;
import com.ppdai.framework.raptor.spring.annotation.RaptorClient;
import okio.ByteString;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IntegrationTest {

    @RaptorClient
    private Simple simple1;

    @RaptorClient
    private MoreService moreService;

    @Test
    public void testRpcCall() {
        HelloRequest helloRequest = new HelloRequest();
        helloRequest.setName("ppdai");
        HelloReply reply = simple1.sayHello(helloRequest);
        System.out.println(reply);
        Assert.assertTrue(StringUtils.startsWith(reply.getMessage(), "Hello ppdai"));
    }

    @Test
    public void testGet1() {
        HelloRequest testObject = getTestMessage();
        HelloReply reply = moreService.testGet1(testObject, "p1");
        //TODO
        System.out.println(reply.getMessage());
    }

    @Test
    public void testGet2() {
        HelloRequest testObject = getTestMessage();
        HelloReply reply = moreService.testGet2(testObject, "p1");
        //TODO

        System.out.println(reply.getMessage());
    }


    @Test
    public void testPost1() {
        HelloRequest testObject = getTestMessage();
        HelloReply reply = moreService.testPost1(testObject, "p1");
        //TODO
        System.out.println(reply.getMessage());
    }

    @Test
    public void testPost2() {
        HelloRequest testObject = getTestMessage();
        HelloReply reply = moreService.testPost2(testObject, "p3", "p1", 2);
        //TODO
        System.out.println(reply.getMessage());
    }

    private HelloRequest getTestMessage() {
        HelloRequest helloRequest = new HelloRequest();
        helloRequest.setName("ppdai");
        helloRequest.setCorpus(HelloRequest.Corpus.UNIVERSAL);
        helloRequest.setSnippets(Arrays.asList("snippets1", "snippets2"));
        helloRequest.setCats(Arrays.asList(new Cat("black"), new Cat("white")));
        helloRequest.setResult(new HelloRequest.Result("http://ppdai.com", HelloRequest.Result.Corpus.NEWS));
        helloRequest.setTbytes(ByteString.encodeUtf8("拍拍贷"));
        helloRequest.setTDouble(1.01);
        helloRequest.setTFloat(1.02f);
        helloRequest.setTfixed32(23);
        helloRequest.setTfixed64(23232222332332323L);
        helloRequest.setTint32(123);
        helloRequest.setTint64(3232132555244324324L);
        helloRequest.setTsfixed32(213);
        helloRequest.setTsfixed64(323232323223232L);
        helloRequest.setTunit32(123);
        helloRequest.setTunit64(323213232133223L);
        helloRequest.setTbool(true);

        helloRequest.setRepFixed32(Arrays.asList(1, 2, 3));
        helloRequest.setRepString(Arrays.asList("str1", "str2"));

        HelloRequest.Result result = new HelloRequest.Result("url1", HelloRequest.Result.Corpus.NEWS);
        helloRequest.setRepResult(Arrays.asList(result, result, result));

        helloRequest.setMapInt32Int32(MapUtils.putAll(new HashMap<>(), new Object[]{1, 2, 3, 4}));
        helloRequest.setMapStringMessage(MapUtils.putAll(new HashMap<>(), new Object[]{"c1", result, "c2", result}));
        helloRequest.setMapStringEnum(MapUtils.putAll(new HashMap<>(), new Object[]{"c1", HelloRequest.Corpus.IMAGES, "c2", HelloRequest.Corpus.PRODUCTS}));

        return helloRequest;
    }
}
