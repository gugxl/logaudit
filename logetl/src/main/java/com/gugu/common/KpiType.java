package com.gugu.common;

/**
 * @author gugu
 * @Classname KpiType
 * @Description TODO
 * @Date 2020/1/2 18:21
 */
public enum KpiType {
    NEW_INSTALL_USER("new_install_user"), // 统计新用户的kpi
    BROWSER_NEW_INSTALL_USER("browser_new_install_user"), // 统计浏览器维度的新用户kpi
    ACTIVE_USER("active_user"), // 统计活跃用户kpi
    BROWSER_ACTIVE_USER("browser_active_user"), // 统计浏览器维度的活跃用户kpi
    ;

    public final String name;
    private KpiType(String name) {
        this.name = name;
    }
    public static KpiType valueOfName(String name){
        for (KpiType kpiType : values()){
            if (kpiType.name.equals(name)){
                return kpiType;
            }
        }
        throw new RuntimeException("指定的name不属于该KpiType枚举类：" + name);
    }
}
