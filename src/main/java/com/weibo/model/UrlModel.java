package com.weibo.model;

import lombok.SneakyThrows;

import java.net.URLEncoder;

public class UrlModel {

    private static String contentUrl = "https://m.weibo.cn/api/container/getIndex?containerid=100103type%3D1%26q%3D";

    private static String commentUrl = "https://m.weibo.cn/comments/hotflow?";

    @SneakyThrows
    public static String getContentUrl(String keyVal, Integer page) {
        keyVal = URLEncoder.encode(keyVal, "UTF-8");
        return contentUrl + keyVal + "&page_type=" + "searchall&" +
                "page=" + page;
    }


    public static String getCommentUrl(CommentModel model) {
        String url;
        if (model.getFirst()) {
            url = commentUrl + "id=" + model.getId() + "&mid=" + model.getMid() + "&max_id_type=0";
        } else {
            url = commentUrl + "cid=" + model.getCid() + "&max_id_type=0";
        }
        if (!"0".equals(model.getMaxId())) url = url + "&max_id=" + model.getMaxId();
        return url;
    }


}
