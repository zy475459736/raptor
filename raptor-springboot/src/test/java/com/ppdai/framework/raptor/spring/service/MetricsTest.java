package com.ppdai.framework.raptor.spring.service;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.ppdai.framework.raptor.metric.MetricContext;
import com.ppdai.framework.raptor.spring.TestApplication;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;

/**
 * @author yinzuolong
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MetricsTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Environment env;

    static int port;

    @BeforeClass
    public static void beforeClass() {
        port = SocketUtils.findAvailableTcpPort();
        System.setProperty("server.port", String.valueOf(port));
    }

    @AfterClass
    public static void afterClass() {
        System.clearProperty("server.port");
    }


    @Test
    public void testMetrics() {
        String p = env.getProperty("server.port");
        for (int i = 0; i < 10; i++) {
            String url = "/more?name=ppdai";
            String response = restTemplate.getForObject(url, String.class);
            System.out.println(response);
        }

        MetricRegistry metricRegistry = MetricContext.getMetricRegistry();
        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
        consoleReporter.report();
    }
}
