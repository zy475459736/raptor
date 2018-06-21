package com.ppdai.framework.raptor.utils;

import com.ppdai.framework.raptor.spring.utils.FeignHelper;
import feign.Feign;
import feign.Request;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author yinzuolong
 */
public class FeignHelperTest {

    @Test
    public void name() {
        Feign.Builder builder = Feign.builder();
        Request.Options options = (Request.Options) FeignHelper.getPrivateField(Feign.Builder.class, builder, "options");
        Assert.assertNotNull(options);
        System.out.println(options.connectTimeoutMillis() + ":" + options.readTimeoutMillis());
    }
}
