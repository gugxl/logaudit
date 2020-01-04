package com.gugu.etl.util;


import com.gugu.common.EventLogConstants;
import com.gugu.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gugu
 * @Classname LoggerUtil
 * @Description 处理日志数据的具体工作类
 * @Date 2020/1/2 20:58
 */
public class LoggerUtil {
    private static final Logger logger = Logger.getLogger(LoggerUtil.class);
    private static IPSeekerExt ipSeekerExt = new IPSeekerExt();

    public static Map<String,String> handleLog(String logText){
        Map<String,String> clientInfo = new HashMap<>();
        if (StringUtils.isNotBlank(logText)){
            String[] splits = logText.trim().split(EventLogConstants.LOG_SEPARTIOR);
            if (splits.length == 4){
                // 日志格式为: ip^A服务器时间^Ahost^A请求参数
                clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_IP,splits[0].trim());
                // 设置服务器时间
                clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME, String.valueOf(TimeUtil.parseNginxServerTime2Long(splits[1].trim())));
                int index = splits[3].indexOf("?");
                if (index > -1){
                    String requestBody = splits[3].substring(index + 1);// 获取请求参数，也就是我们的收集数据
                    // 处理请求参数
                    handleRequestBody(requestBody,clientInfo);
                    // 处理userAgent
                    handleUserAgent(clientInfo);
                    // 处理ip地址
                    handleIP(clientInfo);

                }else {
//                    数据格式异常
                    clientInfo.clear();
                }

            }
        }
        return clientInfo;
    }
    /**
     * @Description 处理ip地址
     * @params
     * @param clientInfo
     * @return void
     * @auther gugu
     */

    public static void  handleIP(Map<String,String> clientInfo){
        if (clientInfo.containsKey(EventLogConstants.LOG_COLUMN_NAME_IP)){
            String ip = clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_IP);
            IPSeekerExt.RegionInfo info = ipSeekerExt.analyticIp(ip);
            if (null != info){
                clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_COUNTRY, info.getCountry());
                clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_PROVINCE, info.getProvince());
                clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_CITY, info.getCity());
            }
        }
    }
    /**
     * @Description 处理浏览器的userAgent信息
     * @params
     * @param clientInfo
     * @return void
     * @auther gugu
     */

    public static void handleUserAgent(Map<String,String> clientInfo){
        if (clientInfo.containsKey(EventLogConstants.LOG_COLUMN_NAME_USER_AGENT)){
            UserAgentUtil.UserAgentInfo info = UserAgentUtil.analyticUserAgent(clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_USER_AGENT));
            if ( null != info){
                clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_OS_NAME, info.getOsName());
                clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_OS_VERSION, info.getOsVersion());
                clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME, info.getBrowserName());
                clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION, info.getBrowserVersion());
            }
        }

    }
    /**
     * @Description 处理请求参数
     * @params 
     * @param requestBody
     * @param clientInfo
     * @return void
     * @auther gugu
     */
    
    private static void handleRequestBody(String requestBody,Map<String,String> clientInfo){
        if (StringUtils.isNotBlank(requestBody)){
            String[] requestParams = requestBody.split("&");
            for (String param : requestParams){
                if (StringUtils.isNotBlank(param)){
                    int index = param.indexOf("=");
                    if (index < 0){
                        logger.warn("没法进行解析参数:" + param + "， 请求参数为:" + requestBody);
                        continue;
                    }
                    String key = null, value = null;
                    try {
                        key = param.substring(0, index);
                        value = URLDecoder.decode(param.substring(index+1),"utf-8");
                        clientInfo.put(key, value);
                    } catch (UnsupportedEncodingException e) {
                        logger.warn("解码操作出现异常", e);
                        continue;
                    }


                }
            }
        }
    }
}
