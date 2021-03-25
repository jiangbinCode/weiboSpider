package com.weibo;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.weibo.entity.InformationBase;
import com.weibo.model.CommentModel;
import com.weibo.model.ConfigModel;
import com.weibo.model.UrlModel;
import com.weibo.service.InformationBaseS;
import com.weibo.util.ReqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class ApplicationRun {

    private Logger logger = LoggerFactory.getLogger(ApplicationRun.class);

    @Autowired
    private ConfigModel configModel;


    private LinkedList<InformationBase> informationBases = new LinkedList<>();
    @Autowired
    private InformationBaseS informationBaseS;


    private List<CommentModel> commentModels = Collections.synchronizedList(new LinkedList<CommentModel>());


    public static void main(String[] args) {
        SpringApplication.run(ApplicationRun.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            logger.info("=========================");
            logger.info("configModel:{}", configModel);
            Thread t1 = new Thread(new CrawlContent());
            t1.setName("爬取内容线程");
            t1.start();
            Thread t2 = new Thread(new crawlComments());
            t2.setName("爬取评论线程");
            t2.start();
        };
    }


    class CrawlContent implements Runnable {

        private int page = 1;


        @Override
        public void run() {
            while (informationBases.size() < configModel.getMaxNum() && page < 20) {
                logger.info("------当前爬取关键字为:{},页面数为:{}------", configModel.getKeyVal(), page);
                JSONObject jo = ReqUtil.defaultRequest(UrlModel.getContentUrl(configModel.getKeyVal(), page), configModel.getCookie());
                if (jo == null) continue;
                parse(jo);
                page++;
            }

        }

        public void parse(JSONObject jo) {
            JSONArray cards = jo.getJSONObject("data").getJSONArray("cards");
            for (Object card : cards) {
                JSONObject object = new JSONObject(card);
                if (!object.containsKey("mblog")) continue;
                JSONObject mblog = object.getJSONObject("mblog");
                String mid = mblog.getStr("mid");
                String text = mblog.getStr("text");
                Integer commentsCount = mblog.getInt("comments_count");
                if (commentsCount > 0) commentModels.add(new CommentModel(mid, mid, "0"));
                JSONObject user = mblog.getJSONObject("user");
                parseUser(user, text);
            }
        }
    }


    class crawlComments implements Runnable {

        @Override
        public void run() {
            while (true) {
                if (commentModels.isEmpty()) continue;
                CommentModel model = commentModels.get(0);
                String commentUrl = UrlModel.getCommentUrl(model);
                JSONObject jo = ReqUtil.defaultRequest(commentUrl, configModel.getCookie());
                if (jo == null) continue;
                if (!jo.containsKey("data")) continue;
                JSONObject data = jo.getJSONObject("data");
                if (!data.containsKey("data")) continue;
                String max_id = data.getStr("max_id");
                if (!"0".equals(max_id)) commentModels.add(new CommentModel(model.getId(), model.getId(), max_id));
                JSONArray data1 = data.getJSONArray("data");
                data1.forEach(x -> {
                    JSONObject object = new JSONObject(x);
                    String text = object.getStr("text");
                    parseUser(object.getJSONObject("user"), text);
                });
                commentModels.remove(0);
            }
        }
    }


    public synchronized void parseUser(JSONObject user, String text) {
        if (informationBases.size() >= configModel.getMaxNum())
            throw new RuntimeException("数据以达标:maxNum=" + configModel.getMaxNum());
        text = StripHTML(text);
        if (text.length() < 5) return;
        InformationBase base = new InformationBase();
        String id = user.getStr("id");
        String screen_name = user.getStr("screen_name");
        String followers_count = user.getStr("followers_count");
        base.setId(id);
        base.setNumberOfFans(followers_count);
        base.setText(text);
        base.setName(screen_name);
        base.setKeyVal(configModel.getKeyVal());
        try {
            informationBases.add(base);
            informationBaseS.save(base);
            logger.info("获取用户数:{}", informationBases.size());
        } catch (Exception e) {
            //...
            logger.error(base.getText());
            informationBases.remove(base);
        }

    }


    public String StripHTML(String str) {
        //如果有双引号将其先转成单引号
        String htmlStr = str.replaceAll("\"", "'");
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
        String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }
}
