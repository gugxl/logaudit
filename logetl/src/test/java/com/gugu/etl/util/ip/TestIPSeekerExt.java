package com.gugu.etl.util.ip;

import com.gugu.etl.util.IPSeekerExt;

import java.util.List;

/**
 * @author gugu
 * @Classname TestIPSeekerExt
 * @Description TODO
 * @Date 2020/1/3 21:07
 */
public class TestIPSeekerExt {
    public static void main(String[] args) {
        IPSeekerExt ipSeekerExt = new IPSeekerExt();
        IPSeekerExt.RegionInfo regionInfo = ipSeekerExt.analyticIp("114.114.114.114");
        System.out.println(regionInfo);
        List<String> ips = ipSeekerExt.getAllIp();
        for (String ip : ips){
            System.out.println(ip + " --- " + ipSeekerExt.analyticIp(ip));
        }
    }
}
