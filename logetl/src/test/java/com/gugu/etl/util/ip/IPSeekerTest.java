package com.gugu.etl.util.ip;

import org.junit.Test;

class IPSeekerTest {
    public static void main(String[] args) {
        IPSeeker instance = IPSeeker.getInstance();
        System.out.println(instance.getCountry("120.197.87.216"));
        System.out.println(instance.getCountry("192.168.87.216"));
        System.out.println(instance.getCountry("127.0.87.216"));
        System.out.println(instance.getCountry("10.0.87.216"));
    }


}