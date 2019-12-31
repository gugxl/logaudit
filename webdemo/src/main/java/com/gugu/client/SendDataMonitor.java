package com.gugu.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author gugu
 * @Classname SendDataMonitor
 * @Description 发送url数据的监控者，用于启动一个单独的线程来发送数据
 * @Date 2019/12/31 10:01
 */
public class SendDataMonitor {
    // 日志记录对象
    private static final Logger logger = Logger.getGlobal();
    // 队列，用户存储发送url
    private BlockingQueue<String> queue = new LinkedBlockingDeque<>();
    // 用于单列的一个类对象
    private static SendDataMonitor monitor = null;
    private SendDataMonitor(){
        // 私有构造方法，进行单列模式的创建
    }

    public static SendDataMonitor getSendDataMonitor(){
        if (null == monitor){
            synchronized(SendDataMonitor.class){
                if(null == monitor){
                    monitor = new SendDataMonitor();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 线程中调用具体的处理方法
                            SendDataMonitor.monitor.run();
                        }
                    });
                    // 测试的时候，不设置为守护模式
//                    thread.setDaemon(true);
                    thread.start();
                }
            }
        }
        return monitor;
    }
    public static void addSendUrl(String url) throws InterruptedException {
        getSendDataMonitor().queue.put(url);
    }
    /**
     * @Description 具体执行发送url的方法
     * @params
     * @return void
     * @auther gugu
     */
    
    private void run(){
        while (true){
            try {
                String url = this.queue.take();
                try {
                    HttpRequestUtil.sendData(url);
                } catch (Throwable e) {
                    logger.log(Level.WARNING, "发送url异常", e);
                }
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "发送url异常", e);
            }

        }

    }
    /**
     * @Description 内部类，用户发送数据的http工具类
     * @auther gugu
     */
    
    public static class HttpRequestUtil{
        public static void sendData(String urlStr) throws IOException {
            HttpURLConnection conn = null;
            BufferedReader in = null;
            try {
                URL url = new URL(urlStr);// 创建url对象
                conn = (HttpURLConnection) url.openConnection();// 打开url连接
                // 设置连接参数
                conn.setConnectTimeout(5000);// 连接过期时间
                conn.setReadTimeout(5000);// 读取数据过期时间
                conn.setRequestMethod("GET");// 设置请求类型为get

                System.out.println("发送url:" + url);
                // 发送连接请求
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                // TODO: 这里考虑是否可以


            } finally {
                if (null != in){
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    conn.disconnect();
                }
            }

        }
    }
}
