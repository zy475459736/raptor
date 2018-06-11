package com.ppdai.framework.raptor.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yinzuolong
 */
public class RaptorContext {

    private Map<Object, Object> attributes = new ConcurrentHashMap<>();
    private Map<String, String> requestAttachments = new ConcurrentHashMap<>();
    private Map<String, String> responseAttachments = new ConcurrentHashMap<>();

    private static final ThreadLocal<RaptorContext> CONTEXT = new ThreadLocal<RaptorContext>() {
        @Override
        protected RaptorContext initialValue() {
            return new RaptorContext();
        }
    };

    public static RaptorContext getContext() {
        return CONTEXT.get();
    }

    public void putAttribute(Object key, Object value) {
        attributes.put(key, value);
    }

    public void putAllAttribute(Map<Object, Object> map) {
        attributes.putAll(map);
    }

    public Object getAttribute(Object key) {
        return attributes.get(key);
    }

    public void revomeAttribute(Object key) {
        attributes.remove(key);
    }

    public Map<Object, Object> getAttributes() {
        return attributes;
    }

    public void putRequestAttachment(String key, String value) {
        requestAttachments.put(key, value);
    }

    public void putAllRequestAttachments(Map<String, String> map) {
        requestAttachments.putAll(map);
    }

    public String getRequestAttachment(String key) {
        return requestAttachments.get(key);
    }

    public void removeRequestAttachment(String key) {
        requestAttachments.remove(key);
    }

    public Map<String, String> getRequestAttachments() {
        return requestAttachments;
    }

    public void putResponseAttachment(String key, String value) {
        responseAttachments.put(key, value);
    }

    public void putAllResponseAttachments(Map<String, String> map) {
        responseAttachments.putAll(map);
    }

    public String getResponseAttachment(String key) {
        return responseAttachments.get(key);
    }

    public void removeResponseAttachment(String key) {
        responseAttachments.remove(key);
    }

    public Map<String, String> getResponseAttachments() {
        return responseAttachments;
    }

}
