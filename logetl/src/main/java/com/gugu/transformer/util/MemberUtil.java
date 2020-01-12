package com.gugu.transformer.util;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gugu
 * @Classname MemberUtil
 * @Description 操作member_info表的工具类，主要作用是判断member id是否是正常的id以及是否是一个新的访问会员id
 * @Date 2020/1/12 15:18
 */
public class MemberUtil {
    private static Map<String, Boolean> cache = new LinkedHashMap<String, Boolean>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
            return this.size() > 10000;// 最多保存1w个数据
        }
    };

    /**
     * @param date
     * @param conn
     * @return void
     * @Description 删除指定日期的数据
     * @params
     * @auther gugu
     */

    public static void deleteMemberInfoByDate(String date, Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM `member_info` WHERE `created` = ?");
            pstmt.setString(1, date);
            pstmt.execute();
        } finally {
            if (null != pstmt) {
                try {
                    pstmt.close();
                } catch (Exception e) {
                    // nothing
                }
            }
        }
    }

    /**
     * @param memberId 需要判断的member id
     * @return boolean
     * @Description 判断member id的格式是否正常，如果不正常，直接方面false。否则返回true。，
     * @params
     * @auther gugu
     */

    public static boolean isValidateMemberId(String memberId) {
        if (StringUtils.isNotBlank(memberId)) {
            // 进行格式判断
            return memberId.trim().matches("[0-9a-zA-Z]{1,32}");
        }
        return false;
    }

    /**
     * @param memberId 需要判断的member id
     * @param conn 数据库连接信息
     * @return boolean
     * @Description 判断memberid是否是一个新会员id，如果是，则返回true。否则返回false。
     * @params
     * @auther gugu
     */

    public static boolean isNewMemberId(String memberId, Connection conn) throws SQLException {
        Boolean isNewMemberId = null;
        if (StringUtils.isNotBlank(memberId)){
            // 要求memberid不为空
            isNewMemberId = cache.get(memberId);
            if (null ==  isNewMemberId){
                // 表示该memberid没有进行数据库查询
                PreparedStatement pstmt = null;
                ResultSet rs = null;
                try {
                    pstmt = conn.prepareStatement("SELECT `member_id`,`last_visit_date` FROM `member_info` WHERE `member_id`=?");
                    pstmt.setString(1, memberId);
                    rs = pstmt.executeQuery();
                    if (rs.next()){
                        // 表示数据库中有对应的member id，那么该memberid不是新的会员id
                        isNewMemberId = Boolean.valueOf(false);
                    }else {
                        // 表示数据库中没有对应的member id，那么表示该memberid是新的的会员id
                        isNewMemberId = Boolean.valueOf(true);
                    }
                    cache.put(memberId, isNewMemberId);
                }finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            // nothing
                        }
                    }
                    if (pstmt != null) {
                        try {
                            pstmt.close();
                        } catch (SQLException e) {
                            // nothing
                        }
                    }
                }
            }
        }
//        结果的返回
        return isNewMemberId == null ? false : isNewMemberId.booleanValue();
    }

}
