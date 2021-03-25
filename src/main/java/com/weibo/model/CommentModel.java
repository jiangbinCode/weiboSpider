package com.weibo.model;

import lombok.Data;

@Data
public class CommentModel {

    private String id;

    private String mid;

    private String cid;

    private String maxId;

    private Boolean first;

    public CommentModel(String id, String mid, String maxId) {
        this.id = id;
        this.mid = mid;
        this.maxId = maxId;
        this.first = true;
    }

    public CommentModel(String cid, String maxId) {
        this.maxId = maxId;
        this.first = false;
        this.cid = cid;
    }

}
