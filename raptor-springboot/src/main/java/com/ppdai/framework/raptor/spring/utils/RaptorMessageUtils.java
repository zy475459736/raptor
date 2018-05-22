package com.ppdai.framework.raptor.spring.utils;

import com.ppdai.framework.raptor.annotation.RaptorField;
import com.ppdai.framework.raptor.annotation.RaptorMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yinzuolong
 */
public class RaptorMessageUtils {


    public static Map<String, List<String>> transferMessageToQuery(Object message) throws Exception {
        RaptorMessage raptorMessage = AnnotationUtils.findAnnotation(message.getClass(), RaptorMessage.class);
        Assert.notNull(raptorMessage, "Object not annotated by @RaptorMessage.");
        Map<String, List<String>> resultMap = new HashMap<>();
        transferMessageToQuery("", resultMap, message);
        return resultMap;
    }

    private static void transferMessageToQuery(String name, Map<String, List<String>> resultMap, Object value) throws Exception {
        if (value == null) {
            return;
        }
        if (List.class.isAssignableFrom(value.getClass())) {
            //List
            List valueList = (List) value;
            for (Object aValueList : valueList) {
                transferMessageToQuery(name, resultMap, aValueList);
            }
        } else if (Map.class.isAssignableFrom(value.getClass())) {
            //Map
            Map valueMap = (Map) value;
            for (Object key : valueMap.keySet()) {
                if (!String.class.isAssignableFrom(key.getClass())) {
                    continue;
                }
                transferMessageToQuery(getNameKey(name, key.toString()), resultMap, valueMap.get(key));
            }
        } else if (isAnnotatedByRaptorMessage(value.getClass())) {
            //Message类型
            Field[] fields = value.getClass().getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                RaptorField wireField = AnnotationUtils.findAnnotation(field, RaptorField.class);
                if (wireField == null) {
                    continue;
                }
                Object filedValue = BeanUtils.getPropertyDescriptor(value.getClass(), fieldName).getReadMethod().invoke(value);
                if (filedValue == null) {
                    continue;
                }
                transferMessageToQuery(getName(name, fieldName), resultMap, filedValue);
            }
        } else {
            //叶子节点，直接放入map
            resultMap.putIfAbsent(name, new ArrayList<>());
            List<String> list = resultMap.get(name);
            list.add(value.toString());
        }
    }


    private static String getNameKey(String name, String key) {
        return name + "[" + key + "]";
    }

    private static String getName(String parent, String name) {
        if (StringUtils.isEmpty(parent)) {
            return name;
        }
        return parent + "." + name;
    }

    private static boolean isAnnotatedByRaptorMessage(Class<?> type) {
        return AnnotationUtils.findAnnotation(type, RaptorMessage.class) != null;
    }
}
