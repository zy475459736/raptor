package com.ppdai.framework.raptor.spring.integration;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.ppdai.framework.raptor.metric.MetricContext;
import com.ppdai.framework.raptor.proto.AllTypesPojo;
import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.proto.SimpleExtension;
import com.ppdai.framework.raptor.spring.TestApplication;
import com.ppdai.framework.raptor.spring.annotation.RaptorClient;
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
    private SimpleExtension simpleExtension;

    @Test
    public void testRpcCall() {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        Helloworld.HelloReply reply = simple1.sayHello(helloRequest);
        System.out.println(reply);
        Assert.assertTrue(StringUtils.startsWith(reply.getMessage(), "Hello ppdai"));

        MetricRegistry metricRegistry = MetricContext.getMetricRegistry();
        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
        consoleReporter.report();
    }

    @Test
    public void testGet1() {
        AllTypesPojo testObject = getTestMessage();
        AllTypesPojo newObject = simpleExtension.testGet1(testObject, "p1");
        //TODO
        System.out.println(newObject.getMapStringMessage());
    }

    @Test
    public void testGet2() {
        AllTypesPojo testObject = getTestMessage();
        AllTypesPojo newObject = simpleExtension.testGet2(testObject, "p1");
        //TODO
        System.out.println(newObject.getMapStringMessage());
    }


    @Test
    public void testPost1() {
        AllTypesPojo testObject = getTestMessage();
        AllTypesPojo newObject = simpleExtension.testPost1(testObject, "p1");
        //TODO
        System.out.println(newObject.getMapStringMessage());
    }

    @Test
    public void testPost2() {
        AllTypesPojo testObject = getTestMessage();
        AllTypesPojo newObject = simpleExtension.testPost2(testObject, "p1",2);
        //TODO
        System.out.println(newObject.getMapStringMessage());
    }

    private AllTypesPojo getTestMessage() {
        AllTypesPojo testObject = new AllTypesPojo();
        testObject.setString("ddd");
        testObject.setInt32(222);
        testObject.setRepBool(Arrays.asList(true, false, false));
        testObject.setDouble_(222.0);
        testObject.setMapInt32Int32(MapUtils.putAll(new HashMap<>(), new Integer[]{1, 2, 3, 4}));
        AllTypesPojo.NestedMessage nest = new AllTypesPojo.NestedMessage();
        nest.setA(123);
        testObject.setRepNestedMessage(Arrays.asList(nest, nest));
        testObject.setMapStringMessage(MapUtils.putAll(new HashMap<>(), new Object[]{"test1", nest, "test2", nest}));
        return testObject;
    }
}
