package com.gugu.etl.util;

import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;
import cz.mallat.uasparser.UserAgentInfo;

import java.io.IOException;

/**
 * @author gugu
 * @Classname UserAgentUtil
 * @Description 解析浏览器的user agent的工具类，内部就是调用这个uasparser jar文件
 * @Date 2020/1/3 13:12
 */
public class UserAgentUtil {
    static UASparser uaSparser  = null;
    // static 代码块, 初始化uasParser对象
    static {
        try {
            uaSparser = new UASparser(OnlineUpdater.getVendoredInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UserAgentInfo analyticUserAgent(String userAgent){
        UserAgentInfo result = null;
        if (!(userAgent == null || userAgent.trim().isEmpty())){
            // 此时userAgent不为null，而且不是由全部空格组成的
            cz.mallat.uasparser.UserAgentInfo info = null;
            try {
                info = uaSparser.parse(userAgent);
                result = new UserAgentInfo();
                result.setBrowserName(info.getUaFamily());
                result.setBrowserVersion(info.getBrowserVersionInfo());
                result.setOsName(info.getOsFamily());
                result.setOsVersion(info.getOsName());
            } catch (IOException e) {
                // 出现异常，将返回值设置为null
                result = null;
            }
        }
        return result;
    }

    public static class UserAgentInfo {
        private String browserName; // 浏览器名称
        private String browserVersion; // 浏览器版本号
        private String osName; // 操作系统名称
        private String osVersion; // 操作系统版本号

        public String getBrowserName() {
            return browserName;
        }

        public void setBrowserName(String browserName) {
            this.browserName = browserName;
        }

        public String getBrowserVersion() {
            return browserVersion;
        }

        public void setBrowserVersion(String browserVersion) {
            this.browserVersion = browserVersion;
        }

        public String getOsName() {
            return osName;
        }

        public void setOsName(String osName) {
            this.osName = osName;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }

        @Override
        public String toString() {
            return "UserAgentInfo [browserName=" + browserName + ", browserVersion=" + browserVersion + ", osName="
                    + osName + ", osVersion=" + osVersion + "]";
        }
    }
}
