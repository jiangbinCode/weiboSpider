package com.weibo.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
@ToString
public class ConfigModel {

    @Value("${config.cookie}")
    private String cookie;

    @Value("${config.keyVal}")
    private String keyVal;

    @Value("${config.maxNum}")
    private Integer maxNum;


}
