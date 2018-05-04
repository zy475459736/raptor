package com.ppdai.framework.raptor.spring.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ppdai.framework.raptor.proto.AllTypesPojo;
import org.junit.Test;

/**
 * @author yinzuolong
 */
public class JsonSerializeTest {

    private final Gson gson = new GsonBuilder()
//            .registerTypeAdapterFactory(new RaptorTypeAdapterFactory())
            .disableHtmlEscaping()
            .create();

    @Test
    public void name() {
        String json = "{\n" +
                "\"int32\":123\n" +
                "}";
        AllTypesPojo allTypesPojo = gson.fromJson(json, AllTypesPojo.class);
        System.out.println(allTypesPojo.getInt32());
    }
}
