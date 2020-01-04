package com.gugu.common;

/**
 * @author gugu
 * @Classname DateEnum
 * @Description 日期类型枚举类
 * @Date 2020/1/2 18:04
 */
public enum DateEnum {
    YEAR("year"), SEASON("season"), MONTH("month"), WEEK("week"), DAY("day"), HOUR(
            "hour");
    public final String name;
    private DateEnum(String name) {
        this.name = name;
    }

    /**
     * @Description 根据属性name的值获取对应的type对象
     * @params
     * @param name
     * @return com.gugu.common.DateEnum
     * @auther gugu
     */

    public static DateEnum valueOfName(String name){
        for (DateEnum type : values()){
            if ( type.name.equals(name)){
                return type;
            }
        }
        return null;
    }
}
