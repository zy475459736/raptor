package com.ppdai.framework.raptor.spring.service;

import com.ppdai.framework.raptor.spring.TestApplication;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author yinzuolong
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RaptorServiceTest {

    private RestTemplate restTemplate = new RestTemplate();

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
        for (int i = 0; i < 10; i++) {
            String url = "http://localhost:" + port + "/more?name=ppdai";
            String response = restTemplate.getForObject(url, String.class);
            System.out.println(response);
            Assert.assertTrue(response.contains("ppdai"));
        }

    }
}
