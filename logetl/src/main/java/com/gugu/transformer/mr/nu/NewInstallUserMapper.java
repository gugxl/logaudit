package com.gugu.transformer.mr.nu;

import com.gugu.common.DateEnum;
import com.gugu.common.EventLogConstants;
import com.gugu.common.KpiType;
import com.gugu.transformer.model.dim.StatsCommonDimension;
import com.gugu.transformer.model.dim.StatsUserDimension;
import com.gugu.transformer.model.dim.base.BrowserDimension;
import com.gugu.transformer.model.dim.base.DateDimension;
import com.gugu.transformer.model.dim.base.KpiDimension;
import com.gugu.transformer.model.dim.base.PlatformDimension;
import com.gugu.transformer.model.value.map.TimeOutputValue;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.omg.CORBA.TIMEOUT;

import java.io.IOException;
import java.util.List;

/**
 * @author gugu
 * @Classname NewInstallUserMapper
 * @Description 自定义的计算新用户的mapper类
 * @Date 2020/1/5 16:45
 */
public class NewInstallUserMapper extends TableMapper<StatsUserDimension, TimeOutputValue> {//每个分析条件（由各个维度组成的）作为key，uuid作为value
    private static final Logger logger = Logger.getLogger(NewInstallUserMapper.class);
    private  StatsUserDimension statsUserDimension = new StatsUserDimension();
    private TimeOutputValue timeOutputValue = new TimeOutputValue();

    private byte[] family = Bytes.toBytes(EventLogConstants.EVENT_LOGS_FAMILY_NAME);

    //代表用户分析模块的统计
    private KpiDimension newInstallUserKpi = new KpiDimension(KpiType.NEW_INSTALL_USER.name);
    //浏览器分析模块的统计
    private KpiDimension newInstallUserOfBrowserKpi = new KpiDimension(KpiType.BROWSER_NEW_INSTALL_USER.name);
    /**
     * @Description 读取hbase中的数据，输入数据为：hbase表中每一行。
     *
     * @params 
     * @param key 输出key类型：StatsUserDimension
     * @param value value类型：TimeOutputValue
     * @param context
     * @return void
     * @auther gugu
     */
    
    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        String uuid = Bytes.toString(value.getValue(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_UUID)));
        String serverTime = Bytes.toString(value.getValue(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME)));
        String platform = Bytes.toString(value.getValue(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_PLATFORM)));
        System.out.println(uuid + "-" + serverTime + "-" + platform);
        if (StringUtils.isBlank(uuid)||StringUtils.isBlank(serverTime) ||StringUtils.isBlank(platform)){
            logger.warn("uuid&servertime&platform不能为空");
            return;
        }

        Long longOfTime = Long.valueOf(serverTime.trim());
        timeOutputValue.setId(uuid);// 设置id为uuid
        timeOutputValue.setTime(longOfTime);// 设置时间为服务器时间
        DateDimension dateDimension = DateDimension.buildDate(longOfTime, DateEnum.DAY);
        // 设置date维度
        StatsCommonDimension statsCommon = this.statsUserDimension.getStatsCommon();
        statsCommon.setDate(dateDimension);

        List<PlatformDimension> platformDimensions = PlatformDimension.buildList(platform);
        // browser相关的数据
        String browserName = Bytes.toString(value.getValue(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME)));
        String browserVersion = Bytes.toString(value.getValue(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION)));
        List<BrowserDimension> browserDimensions = BrowserDimension.buildList(browserName, browserVersion);
        //空浏览器维度，不考虑浏览器维度
        BrowserDimension defaultBrowser = new BrowserDimension("", "");
        for (PlatformDimension pd : platformDimensions){
            statsUserDimension.setBrowser(defaultBrowser);
            statsCommon.setKpi(newInstallUserKpi);
            statsCommon.setPlatform(pd);
            context.write(statsUserDimension, timeOutputValue);
            for (BrowserDimension browserDimension :browserDimensions){
                statsCommon.setKpi(newInstallUserOfBrowserKpi);
                statsUserDimension.setBrowser(browserDimension);
                context.write(statsUserDimension, timeOutputValue);
            }
        }
    }
}
