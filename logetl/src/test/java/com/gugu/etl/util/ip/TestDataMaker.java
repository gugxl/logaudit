package com.gugu.etl.util.ip;

import com.gugu.common.EventLogConstants;
import com.gugu.etl.util.IPSeekerExt;
import com.gugu.etl.util.LoggerUtil;
import javafx.scene.control.Tab;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.CRC32;

/**
 * @author gugu
 * @Classname TestDataMaker
 * @Description TODO
 * @Date 2020/1/3 21:40
 */
public class TestDataMaker {
    // 表名
    private static String TN = "eventlog";

    public static void main(String[] args) throws Exception {
        TestDataMaker tDataMaker = new TestDataMaker();
        Random random = new Random();
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("base.zookeeper.quorum","master,slave1,slave2");
        Connection connection = ConnectionFactory.createConnection(configuration);
        Admin admin = connection.getAdmin();
        Table table = connection.getTable(TableName.valueOf(TN));
        // 用户标示u_ud  随机生成8位
        String uuid = String.format("%08d", random.nextInt(99999999));
        // 会员标示u_mid  随机生成8位
        String memberId = String.format("%08d", random.nextInt(99999999));
        List<Put> puts = new ArrayList<Put>();
        for (int i = 0; i< 100; i++){
            if(i%5==0) {
                uuid = String.format("%08d", random.nextInt(99999999));
                memberId = String.format("%08d", random.nextInt(99999999));
            }
            if(i%6==0) {
                uuid = String.format("%08d", random.nextInt(99999999));
                memberId = String.format("%08d", random.nextInt(99999999));
            }

            Date d = tDataMaker.getDate("20170823");

            String serverTime = ""+d.getTime();

            Put put = tDataMaker.putMaker(uuid, memberId, serverTime);
            puts.add(put);
        }
        table.put(puts);
    }

    private static IPSeekerExt ipSeekerExt = new IPSeekerExt();
    Random r = new Random();

    public Put putMaker(String uuid, String memberId,String serverTime){
        Map<String, Put> map = new HashMap<String, Put>();
        byte[] family = Bytes.toBytes(EventLogConstants.EVENT_LOGS_FAMILY_NAME);
        // 解析日志
        Map<String, String> clientInfo = LoggerUtil.handleLog("......");
        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME, serverTime);
        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_UUID, uuid);
        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_PLATFORM, "website");

        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_EVENT_NAME, EventNames[r.nextInt(EventNames.length)]);
        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_SESSION_ID, SessionIDs[r.nextInt(SessionIDs.length)]);
        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_CURRENT_URL, CurrentURLs[r.nextInt(CurrentURLs.length)]);


        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_OS_NAME, this.getOsName());
        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_OS_VERSION, this.getOsVersion());
        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME, this.getBrowserName());
        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION, this.getBrowserVersion());

        String ip = IPs[r.nextInt(IPs.length)];
        com.gugu.etl.util.IPSeekerExt.RegionInfo info = ipSeekerExt.analyticIp(ip);
        if (info != null) {
            clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_COUNTRY, info.getCountry());
            clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_PROVINCE, info.getProvince());
            clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_CITY, info.getCity());
        }

        String eventName = EventNames[r.nextInt(EventNames.length)];

        //生成rowkey
        String rowkey = this.generateRowKey(uuid, memberId, eventName, serverTime);

        Put put = new Put(Bytes.toBytes(rowkey));
        for (Map.Entry<String, String> entry : clientInfo.entrySet()) {
            put.addColumn(family, Bytes.toBytes(entry.getKey()), Bytes.toBytes(entry.getValue()));
        }

        return put;
    }

    private String[] CurrentURLs = new String[]{"http://www.jd.com",
            "http://www.tmall.com","http://www.sina.com","http://www.weibo.com"};

    private String[] SessionIDs = new String[]{"1A3B4F83-6357-4A64-8527-F092169746D3",
            "12344F83-6357-4A64-8527-F09216974234","1A3B4F83-6357-4A64-8527-F092169746D8"};

    private String[] IPs = new String[]{"58.42.245.255","39.67.154.255",
            "23.13.191.255","14.197.148.38","14.197.149.137","14.197.201.202","14.197.243.254"};

    private String[] EventNames = new String[]{"e_l","e_pv"};

    private String[] BrowserNames = new String[]{"FireFox","Chrome","aoyou","360"};
    /**
     * 获取随机的浏览器名称
     * @return
     */
    private String getBrowserName() {
        return BrowserNames[r.nextInt(BrowserNames.length)];
    }
    /**
     * 获取随机的浏览器版本信息
     * @return
     */
    private String getBrowserVersion() {
        return (""+r.nextInt(9));
    }

    /**
     * 获取随机的系统版本信息
     * @return
     */
    private String getOsVersion() {
        return (""+r.nextInt(3));
    }

    private String[] OsNames = new String[]{"window","linux","ios"};
    /**
     * 获取随机的系统信息
     * @return
     */
    private String getOsName() {
        return OsNames[r.nextInt(OsNames.length)];
    }

    private CRC32 crc32 = new CRC32();

    /**
     * @Description 根据uuid memberid servertime创建rowkey
     * @params 
     * @param uuid
     * @param memberId
     * @param eventAliasName
     * @param serverTime
     * @return java.lang.String
     * @auther gugu
     */
    private String generateRowKey(String uuid, String memberId, String eventAliasName, String serverTime){
        StringBuilder sb = new StringBuilder();
        sb.append(serverTime).append("_");
        this.crc32.reset();
        if (StringUtils.isNotBlank(uuid)) {
            this.crc32.update(uuid.getBytes());
        }
        if (StringUtils.isNotBlank(memberId)) {
            this.crc32.update(memberId.getBytes());
        }
        this.crc32.update(eventAliasName.getBytes());
        sb.append(this.crc32.getValue() % 100000000L);
        return sb.toString();
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * @Description 随机生成时间
     * @params 
     * @param str 年月日 20160101
     * @return java.util.Date
     * @auther gugu
     */
    
    public Date getDate(String str) {
        str = str + String.format("%02d%02d%02d", new Object[]{r.nextInt(24), r.nextInt(60), r.nextInt(60)});
        Date d = new Date();
        try {
            d = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}
