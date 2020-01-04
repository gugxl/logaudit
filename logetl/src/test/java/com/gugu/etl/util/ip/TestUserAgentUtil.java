package com.gugu.etl.util.ip;

import com.gugu.etl.util.UserAgentUtil;

/**
 * @author gugu
 * @Classname TestUserAgentUtil
 * @Description TODO
 * @Date 2020/1/3 21:36
 */
public class TestUserAgentUtil {
    public static void main(String[] args) {
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36";
        String userAgent1 = "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; GWX:QUALIFIED; rv:11.0) like Gecko";
        String userAgent2 = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0";
        UserAgentUtil.UserAgentInfo info = UserAgentUtil.analyticUserAgent(userAgent);
        UserAgentUtil.UserAgentInfo info1 = UserAgentUtil.analyticUserAgent(userAgent1);
        UserAgentUtil.UserAgentInfo info2 = UserAgentUtil.analyticUserAgent(userAgent2);
        System.out.println(info);
        System.out.println(info1);
        System.out.println(info2);
    }
}
