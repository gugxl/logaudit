package com.gugu.transformer.serice.impl;

import com.gugu.transformer.model.dim.base.BaseDimension;
import com.gugu.transformer.model.dim.base.BrowserDimension;
import com.gugu.transformer.model.dim.base.DateDimension;
import com.gugu.transformer.model.dim.base.PlatformDimension;
import com.gugu.transformer.serice.IDimensionConverter;
import com.sun.java.browser.plugin2.DOM;
import org.apache.hadoop.hbase.types.RawBytesTerminated;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gugu
 * @Classname DimensionConverterImpl
 * @Description TODO
 * @Date 2020/1/5 9:42
 */
public class DimensionConverterImpl implements IDimensionConverter {
    private static final Logger logger = Logger.getLogger(DimensionConverterImpl.class);
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://master:3306/result_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private Map<String, Integer> cache = new LinkedHashMap<String,Integer>(){
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
            return this.size() > 5000;
        }
    };
    static{
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        }
    }

    @Override
    public int getDimensionIdByValue(BaseDimension dimension) throws IOException {
        String cacheKey = this.buildCacheKey(dimension);// 获取cache key
        System.out.println("————————维度的cache key +"+cacheKey);
        if (this.cache.containsKey(cacheKey)){
            return this.cache.get(cacheKey);
        }

        Connection conn = null;
        // 1. 查看数据库中是否有对应的值，有则返回
        // 2. 如果第一步中，没有值；先插入我们dimension数据， 获取id
        String[] sql = null;// 具体执行sql数组
        if (dimension instanceof DateDimension){
            sql = this.buildDateSql();
        }else if (dimension instanceof PlatformDimension){
            sql = this.buildPlatformSql();
        }else if (dimension instanceof BrowserDimension){
            sql = this.buildBrowserSql();
        }else {
            throw new IOException("不支持此dimensionid的获取:" + dimension.getClass());
        }
        try {
            conn = getConnection();
            //            conn=JdbcManager.getConnection(conf, flag)
            int id = 0;
            synchronized (this){
                id = this.executeSql(conn,cacheKey,sql,dimension);
                this.cache.put(cacheKey,id);
            }
            return id;
        } catch (Throwable e) {
            logger.error("操作数据库出现异常", e);
            throw new IOException(e);
        }finally {
            if (conn != null){
                try {
                    conn.close();
                } catch (SQLException e) {

                }
            }
        }
    }
//获取数据库connection连接
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL,USERNAME,PASSWORD);
    }
    private String buildCacheKey(BaseDimension dimension){
        StringBuilder sb = new StringBuilder();
        if (dimension instanceof DateDimension){
            sb.append("date_dimension");
            DateDimension date = (DateDimension) dimension;
            sb.append(date.getYear()).append(date.getSeason()).append(date.getMonth())
                    .append(date.getWeek()).append(date.getDay()).append(date.getType());
        }else if (dimension instanceof PlatformDimension){
            sb.append("platform_dimension");
            PlatformDimension platform = (PlatformDimension) dimension;
            sb.append(platform.getPlatformName());
        }else if (dimension instanceof BrowserDimension){
            sb.append("browser_dimension");
            BrowserDimension browser = (BrowserDimension) dimension;
            sb.append(browser.getBrowserName()).append(browser.getBrowserVersion());
        }

        if (sb.length() == 0){
            throw new RuntimeException("无法创建指定dimension的cachekey：" + dimension.getClass());
        }
        return sb.toString();
    }

    private void setArgs(PreparedStatement pstmt, BaseDimension dimension) throws SQLException {
        int i = 0;
        if (dimension instanceof DateDimension){
            DateDimension date = (DateDimension) dimension;
            pstmt.setInt(++i,date.getYear());
            pstmt.setInt(++i,date.getSeason());
            pstmt.setInt(++i,date.getMonth());
            pstmt.setInt(++i,date.getWeek());
            pstmt.setInt(++i,date.getDay());
            pstmt.setString(++i,date.getType());
            pstmt.setDate(++i, new Date(date.getCalendar().getTime()));
        } else if (dimension instanceof PlatformDimension){
            PlatformDimension platform = (PlatformDimension) dimension;
            pstmt.setString(++i,platform.getPlatformName());
        }else if(dimension instanceof BrowserDimension){
            BrowserDimension browser = (BrowserDimension) dimension;
            pstmt.setString(++i, browser.getBrowserName());
            pstmt.setString(++i, browser.getBrowserVersion());
        }
    }
    /**
     * @Description 创建date dimension相关sql
     * @params
     * @return java.lang.String[]
     * @auther gugu
     */

    private String[] buildDateSql(){
        String querySql = "SELECT `id` FROM `dimension_date` WHERE `year` = ? AND `season` = ? AND `month` = ? AND `week` = ? AND `day` = ? AND `type` = ? AND `calendar` = ?";
        String insertSql = "INSERT INTO `dimension_date`(`year`, `season`, `month`, `week`, `day`, `type`, `calendar`) VALUES(?, ?, ?, ?, ?, ?, ?)";
        return new String[]{querySql,insertSql};

    }

    /**
     * @Description 创建polatform dimension相关sql
     * @params
     * @return java.lang.String[]
     * @auther gugu
     */
    
    private String[] buildPlatformSql(){
        String querySql = "SELECT `id` FROM `dimension_platform` WHERE `platform_name` = ?";
        String insertSql = "INSERT INTO `dimension_platform`(`platform_name`) VALUES(?)";
        return new String[]{querySql,insertSql};
    }
    private String[] buildBrowserSql(){
        String querySql = "SELECT `id` FROM `dimension_browser` WHERE `browser_name` = ? AND `browser_version` = ?";
        String insertSql = "INSERT INTO `dimension_browser`(`browser_name`, `browser_version`) VALUES(?, ?)";
        return new String[] { querySql, insertSql };
    }


    private int executeSql(Connection conn , String cacheKey,String[] sqls, BaseDimension dimension) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sqls[0]);
            // 设置参数
            this.setArgs(pstmt, dimension);
            rs = pstmt.executeQuery();
            if (rs.next()){
                return rs.getInt(1);// 返回值
            }
            // 代码运行到这儿，表示该dimension在数据库中不存储，进行插入
            pstmt = conn.prepareStatement(sqls[1],Statement.RETURN_GENERATED_KEYS);
            // 设置参数
            this.setArgs(pstmt, dimension);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();// 获取返回的自动生成的id
            if (rs.next()){
                return rs.getInt(1); // 获取返回值
            }
        } finally {
            if (rs != null){
                rs.close();
            }
            if (pstmt != null){
                pstmt.close();
            }
        }
        throw new RuntimeException("从数据库获取id失败");
    }
}
