package com.weibo.model;

import lombok.Data;

@Data
public class ContentModel {

    private String id;

    private String mid;

    private String text;

    private UserModel userModel;

}
