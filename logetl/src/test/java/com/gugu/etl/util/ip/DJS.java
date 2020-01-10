package com.gugu.etl.util.ip;

import org.apache.hadoop.hbase.util.Bytes;

import java.util.Calendar;
import java.util.Date;

/**
 * @author gugu
 * @Classname DJS
 * @Description TODO
 * @Date 2020/1/5 11:35
 */
public class DJS {
    public static void main(String[] args) throws InterruptedException {
        djs();
//        testGetBytes();


    }

    private static void testGetBytes() {
        long time = new Date().getTime();
        System.out.println(Bytes.toBytes(""+time).length);
        System.out.println(Bytes.toBytes(time).length);
    }

    private static void djs() throws InterruptedException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        while (true ) {
            int m = (int) ((calendar.getTimeInMillis() - new Date().getTime())/1000);
            System.out.println("倒计时......");
            System.out.println("秒："+m);
            System.out.println("分："+m/60);
            System.out.println("小时："+m/(60*60));
            Thread.sleep(5000);
        }
    }
}
