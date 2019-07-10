package org.nutz.boot.starter.literpc.impl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;

public class RpcObjectInvoker {

    public Map<String, RpcInvoker> invokers = new LinkedHashMap<>();
    
    public String toJson() {
        List<String> methodSigns = new LinkedList<>();
        for (RpcInvoker invoker : invokers.values()) {
            methodSigns.add(invoker.methodSign);
        }
        return Json.toJson(methodSigns);
    }
}
