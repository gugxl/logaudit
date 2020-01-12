package com.gugu.transformer.mr.sessions;

import com.gugu.common.GlobalConstants;
import com.gugu.transformer.model.dim.StatsUserDimension;
import com.gugu.transformer.model.dim.base.BaseDimension;
import com.gugu.transformer.model.dim.base.KpiDimension;
import com.gugu.transformer.model.value.BaseStatsValueWritable;
import com.gugu.transformer.model.value.reduce.MapWritableValue;
import com.gugu.transformer.mr.IOutputCollector;
import com.gugu.transformer.serice.IDimensionConverter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author gugu
 * @Classname SessionsCollector
 * @Description TODO
 * @Date 2020/1/12 18:05
 */
public class SessionsCollector implements IOutputCollector {
    @Override
    public void collect(Configuration conf, BaseDimension key, BaseStatsValueWritable value, PreparedStatement pstmt, IDimensionConverter converter) throws SQLException, IOException {
        StatsUserDimension statsUser = (StatsUserDimension) key;
        MapWritableValue mapWritableValue = (MapWritableValue) value;
        MapWritable map = mapWritableValue.getValue();
        // 设置value
        int i = 0;
        switch (mapWritableValue.getKpi()) {
            case HOURLY_SESSIONS_LENGTH:
            case HOURLY_SESSIONS:
                pstmt.setInt(++i, converter.getDimensionIdByValue(statsUser.getStatsCommon().getPlatform()));
                pstmt.setInt(++i, converter.getDimensionIdByValue(statsUser.getStatsCommon().getDate()));
                pstmt.setInt(++i, converter.getDimensionIdByValue(new KpiDimension(mapWritableValue.getKpi().name)));
                // 设置每个小时的情况
                for (i++; i < 28; i++) {
                    int v = ((IntWritable) map.get(new IntWritable(i - 4))).get();
                    pstmt.setInt(i, v);
                    pstmt.setInt(i + 25, v);
                }

                pstmt.setString(i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
                break;
            case SESSIONS:
                int sessions = ((IntWritable) (map.get(new IntWritable(-1)))).get();
                int sessionsLength = ((IntWritable) (map.get(new IntWritable(-2)))).get();
                pstmt.setInt(++i, converter.getDimensionIdByValue(statsUser.getStatsCommon().getPlatform()));
                pstmt.setInt(++i, converter.getDimensionIdByValue(statsUser.getStatsCommon().getDate()));
                pstmt.setInt(++i, sessions); // 会话个数
                pstmt.setInt(++i, sessionsLength); // 会话长度
                pstmt.setString(++i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
                pstmt.setInt(++i, sessions); // 会话个数
                pstmt.setInt(++i, sessionsLength); // 会话长度
                break;
            case BROWSER_SESSIONS:
                sessions = ((IntWritable) (map.get(new IntWritable(-1)))).get();
                sessionsLength = ((IntWritable) (map.get(new IntWritable(-2)))).get();
                pstmt.setInt(++i, converter.getDimensionIdByValue(statsUser.getStatsCommon().getPlatform()));
                pstmt.setInt(++i, converter.getDimensionIdByValue(statsUser.getStatsCommon().getDate()));
                pstmt.setInt(++i, converter.getDimensionIdByValue(statsUser.getBrowser()));
                pstmt.setInt(++i, sessions); // 会话个数
                pstmt.setInt(++i, sessionsLength); // 会话长度
                pstmt.setString(++i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
                pstmt.setInt(++i, sessions); // 会话个数
                pstmt.setInt(++i, sessionsLength); // 会话长度
                break;
            default:
                throw new RuntimeException("不支持该kpi的输出" + mapWritableValue.getKpi());
        }

        // 添加batch
        pstmt.addBatch();
    }
}
