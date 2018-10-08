package netty.http.worker.controller;

import com.alibaba.fastjson.JSONObject;
import netty.http.server.annotation.JsonParam;
import netty.http.server.annotation.PathParam;
import netty.http.server.annotation.RequestParam;
import netty.http.server.annotation.RouterMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shirukai on 2018/9/30
 * controller
 */
public class TestController {
    /**
     * 测试GET请求
     *
     * @param name name
     * @param id   id
     * @return map
     */
    @RouterMapping(api = "/api/v1/test/get/{id}", method = "GET")
    public Map<String, Object> testGet(
            @RequestParam("name") String name,
            @PathParam("id") String id
    ) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("name", name);
        map.put("id", id);
        return map;
    }

    /**
     * 测试POST请求
     *
     * @param json json
     * @return json
     */
    @RouterMapping(api = "/api/v1/test/post", method = "POST")
    public JSONObject testPost(
            @JsonParam("json") JSONObject json
    ) {
        return json;
    }
}
