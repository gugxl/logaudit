package com.gugu.transformer.mr.inbound;

import com.gugu.common.GlobalConstants;
import com.gugu.transformer.model.dim.StatsInboundDimension;
import com.gugu.transformer.model.dim.base.BaseDimension;
import com.gugu.transformer.model.value.BaseStatsValueWritable;
import com.gugu.transformer.model.value.reduce.InboundReduceValue;
import com.gugu.transformer.mr.IOutputCollector;
import com.gugu.transformer.serice.IDimensionConverter;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author gugu
 * @Classname InboundCollector
 * @Description TODO
 * @Date 2020/1/10 21:38
 */
public class InboundCollector implements IOutputCollector {

    @Override
    public void collect(Configuration conf, BaseDimension key, BaseStatsValueWritable value, PreparedStatement pstmt, IDimensionConverter converter) throws SQLException, IOException {
        StatsInboundDimension inboundDimension = (StatsInboundDimension) key;
        InboundReduceValue inboundReduceValue = (InboundReduceValue) value;

        int i = 0;
        pstmt.setInt(++i, converter.getDimensionIdByValue(inboundDimension.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getDimensionIdByValue(inboundDimension.getStatsCommon().getDate()));
        pstmt.setInt(++i, inboundDimension.getInbound().getId()); // 直接设置，在mapper类中已经设置
        pstmt.setInt(++i, inboundReduceValue.getUvs());
        pstmt.setInt(++i, inboundReduceValue.getVisit());
        pstmt.setString(++i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
        pstmt.setInt(++i, inboundReduceValue.getUvs());
        pstmt.setInt(++i, inboundReduceValue.getVisit());

        pstmt.addBatch();
    }
}
