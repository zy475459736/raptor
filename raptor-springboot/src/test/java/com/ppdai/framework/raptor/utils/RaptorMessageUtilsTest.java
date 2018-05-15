package com.ppdai.framework.raptor.utils;

import com.ppdai.framework.raptor.proto.AllTypesPojo;
import com.ppdai.framework.raptor.spring.utils.RaptorMessageUtils;
import org.apache.commons.collections4.MapUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yinzuolong
 */
public class RaptorMessageUtilsTest {

    @Test
    public void testTransfer() throws Exception {

        AllTypesPojo newObject = new AllTypesPojo();
        newObject.setString("ddd");
        newObject.setInt32(222);
        newObject.setRepBool(Arrays.asList(true, false, false));
        newObject.setDouble_(222.0);
        newObject.setMapInt32Int32(MapUtils.putAll(new HashMap<>(), new Integer[]{1, 2, 3, 4}));
        AllTypesPojo.NestedMessage nest = new AllTypesPojo.NestedMessage();
        nest.setA(123);
        newObject.setRepNestedMessage(Arrays.asList(nest, nest));
        newObject.setMapStringMessage(MapUtils.putAll(new HashMap<>(), new Object[]{"test1", nest, "test2", nest}));

        Map<String, List<String>> map = RaptorMessageUtils.transferMessageToQuery(newObject);
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
}
