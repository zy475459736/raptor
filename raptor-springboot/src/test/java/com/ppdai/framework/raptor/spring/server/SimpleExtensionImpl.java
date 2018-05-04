package com.ppdai.framework.raptor.spring.server;

import com.ppdai.framework.raptor.proto.AllTypesPojo;
import com.ppdai.framework.raptor.proto.SimpleExtension;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yinzuolong
 */
@RestController
public class SimpleExtensionImpl implements SimpleExtension {

    @RequestMapping("/test1")
    public String test1() {
        return "OK";
    }


    @RequestMapping("/test2")
    public Map<String, String> test2() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "123");
        return map;
    }

    @RequestMapping(value = "/test3", method = RequestMethod.POST)
    public Map<String, String> test3(User user) {
        Map<String, String> map = new HashMap<>();
        map.put("a", "123");
        return map;
    }

    @Data
    public static class User {
        private String name;
        private int age;
    }

    @Override
    public AllTypesPojo testGet1(AllTypesPojo request) {
        return request;
    }

    @Override
    public AllTypesPojo testGet2(AllTypesPojo request, String p1) {
        return request;
    }

    @Override
    public AllTypesPojo testPost1(AllTypesPojo request) {
        return request;
    }

    @Override
    public AllTypesPojo testPost2(AllTypesPojo request, String p1, @PathVariable("p2") int p2) {
        AllTypesPojo newObject = new AllTypesPojo();
        newObject.setString("ddd");
        newObject.setInt32(222);
        newObject.setRepBool(Arrays.asList(true, false, false));
        newObject.setDouble_(222.0);
        newObject.setMapInt32Int32(MapUtils.putAll(new HashMap<>(), new Integer[]{1, 2, 3, 4}));
        return newObject;
    }

    @Override
    public AllTypesPojo testPut1(AllTypesPojo request) {
        return request;
    }

    @Override
    public AllTypesPojo testDelete1(AllTypesPojo request, @PathVariable("p2") int p2) {
        return request;
    }
}
