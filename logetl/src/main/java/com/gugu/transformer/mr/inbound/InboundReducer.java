package com.gugu.transformer.mr.inbound;

import com.gugu.common.KpiType;
import com.gugu.transformer.model.dim.StatsInboundDimension;
import com.gugu.transformer.model.value.map.TextsOutputValue;
import com.gugu.transformer.model.value.reduce.InboundReduceValue;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author gugu
 * @Classname InboundReducer
 * @Description 计算reducer类
 * @Date 2020/1/12 9:38
 */
public class InboundReducer extends Reducer<StatsInboundDimension, TextsOutputValue,StatsInboundDimension, InboundReduceValue> {
    private Set<String> uvs = new HashSet<String>();
    private Set<String> visits = new HashSet<String>();
    private InboundReduceValue outputValue = new InboundReduceValue();

    @Override
    protected void reduce(StatsInboundDimension key, Iterable<TextsOutputValue> values, Context context) throws IOException, InterruptedException {
        try {
            for (TextsOutputValue value : values){
                this.uvs.add(value.getUuid());
                this.visits.add(value.getSid());
            }

            this.outputValue.setKpi(KpiType.valueOfName(key.getStatsCommon().getKpi().getKpiName()));
            this.outputValue.setUvs(this.uvs.size());
            this.outputValue.setVisit(this.visits.size());
            context.write(key, this.outputValue);
        } finally {
            // 清空操作
            this.uvs.clear();
            this.visits.clear();
        }
    }
}
