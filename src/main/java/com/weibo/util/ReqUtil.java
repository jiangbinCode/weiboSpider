package com.weibo.util;

import cn.hutool.json.JSONObject;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;


public class ReqUtil {


    @SneakyThrows
    public static JSONObject defaultRequest(String url, String cookie) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Referer", "https://m.weibo.cn");
            httpHeaders.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            httpHeaders.add("Cookie", cookie);
            HttpEntity formEntity = new HttpEntity<>(httpHeaders);
            RestTemplate restTemplate = new RestTemplate();
            Thread.sleep(1500L);
            String object = restTemplate.postForObject(url, formEntity, String.class);
            return new JSONObject(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

}
