package com.weibo.entity;

import lombok.Data;
import org.jsoup.nodes.Document;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "info_base")
@Data
public class InformationBase {

    @Id
    private String id;

    private String name;

    private String text;

    private String numberOfFans;

    private String birthday;

    private Integer age;

    private String school;

    private String area;

    private String keyVal;


    public void setText(String text) {
        this.text = text;
    }


}
