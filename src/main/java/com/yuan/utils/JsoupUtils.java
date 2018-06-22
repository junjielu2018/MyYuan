package com.yuan.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * @author lujunjie
 * @date 2018/6/21 12:04
 */
public class JsoupUtils {
    public static Document getDocument(String url){
        // 获取connection
        Connection con = Jsoup.connect(url);
        // 配置模拟浏览器
        con.header(HttpClientUtils.USER_AGENT, HttpClientUtils.USER_AGENT_VALUE);
        // 获取响应
        Connection.Response rs = null;
        try {
            rs = con.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 转换为Dom树
        Document d1 = Jsoup.parse(rs.body());
        return d1;
    }
}
