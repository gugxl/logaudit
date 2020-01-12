package com.gugu.transformer.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author gugu
 * @Classname UrlUtil
 * @Description url工具类
 * @Date 2020/1/12 14:46
 */
public class UrlUtil {
    /**
     * @Description 判断指定的host是否是一个有效的外链host，如果不是，那么直接返回false，如果是，返回true。
     * @params
     * @param host
     * @return boolean
     * @auther gugu
     */
    public static boolean isValidateInboundHost(String host){
        if ("www.gugu.com".equals(host) || "www.igugu.com".equals(host)) {
            return false;
        }
        return true;
    }
    /**
     * @Description 获取指定url字符串中的host
     * @params 
     * @param url
     * @return java.lang.String
     * @auther gugu
     */
    
    public static String getHost(String url) throws MalformedURLException {
        URL u = getURL(url);
        return u.getHost();
    }

    /**
     * @Description 根据字符串url创建一个URL对象
     * @params 
     * @param url
     * @return URL
     * @auther gugu
     */
    
    public static URL getURL(String url) throws MalformedURLException {
        url = url.trim();
        if (!(url.startsWith("http:") || url.startsWith("https:"))){
            url = "http://" + url;
        }
        return new URL(url);
    }
}
