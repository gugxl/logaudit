package com.gugu.transformer.mr.inbound.bounce;

import com.gugu.common.GlobalConstants;
import com.gugu.transformer.model.dim.StatsInboundDimension;
import com.gugu.transformer.model.dim.base.BaseDimension;
import com.gugu.transformer.model.value.BaseStatsValueWritable;
import com.gugu.transformer.model.value.reduce.InboundBounceReduceValue;
import com.gugu.transformer.model.value.reduce.InboundReduceValue;
import com.gugu.transformer.mr.IOutputCollector;
import com.gugu.transformer.serice.IDimensionConverter;
import jdk.nashorn.internal.objects.Global;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author gugu
 * @Classname InboundBounceCollector
 * @Description TODO
 * @Date 2020/1/12 10:10
 */
public class InboundBounceCollector implements IOutputCollector {

    @Override
    public void collect(Configuration conf, BaseDimension key, BaseStatsValueWritable value, PreparedStatement pstmt, IDimensionConverter converter) throws SQLException, IOException {
        StatsInboundDimension inboundDimension = (StatsInboundDimension) key;
        InboundBounceReduceValue inboundBounceReduceValue = (InboundBounceReduceValue) value;
        int i =0;
        pstmt.setInt(++i, converter.getDimensionIdByValue(inboundDimension.getStatsCommon().getPlatform()));
        pstmt.setInt(++i,converter.getDimensionIdByValue(inboundDimension.getStatsCommon().getDate()));
        pstmt.setInt(++i, inboundDimension.getInbound().getId());
        pstmt.setInt(++i, inboundBounceReduceValue.getBounceNumber());
        pstmt.setString(++i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
        pstmt.setInt(++i, inboundBounceReduceValue.getBounceNumber());

        pstmt.addBatch();
    }
}
