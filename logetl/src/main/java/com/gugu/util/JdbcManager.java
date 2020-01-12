package com.gugu.util;

import com.gugu.common.GlobalConstants;
import org.apache.hadoop.conf.Configuration;

import java.sql.*;

/**
 * @author gugu
 * @Classname JdbcManager
 * @Description jdbc管理
 * @Date 2020/1/2 20:20
 */
public class JdbcManager {
    /**
     * @Description 根据配置获取获取关系型数据库的jdbc连接
     * @params 
     * @param conf hadoop配置信息
     * @param flag 区分不同数据源的标志位
     * @return java.sql.Connection
     * @auther gugu
     */
    
    public static Connection getConnection(Configuration conf, String flag) throws SQLException {
        String driverStr = String.format(GlobalConstants.JDBC_DRIVER, flag);
        String urlStr = String.format(GlobalConstants.JDBC_URL, flag);
        String usernameStr = String.format(GlobalConstants.JDBC_USERNAME, flag);
        String passwordStr = String.format(GlobalConstants.JDBC_PASSWORD, flag);
        String driverClass = conf.get(driverStr);
        String url = conf.get(urlStr);
        String username = conf.get(usernameStr);
        String password = conf.get(passwordStr);
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(url, username, password);
    }
    public static void close(Connection conn, Statement stmt, ResultSet rs){
        if (null != rs){
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
        if (null != stmt){
            try {
                stmt.close();
            } catch (SQLException e) {
            }
        }
        if (null != conn){
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }
}
