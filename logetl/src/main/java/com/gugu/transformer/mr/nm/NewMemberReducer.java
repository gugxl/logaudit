package com.gugu.transformer.mr.nm;

import com.gugu.common.KpiType;
import com.gugu.transformer.model.dim.StatsUserDimension;
import com.gugu.transformer.model.value.map.TimeOutputValue;
import com.gugu.transformer.model.value.reduce.MapWritableValue;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author gugu
 * @Classname NewMemberReducer
 * @Description 计算新增会员的reducer类，其实就是统计每一组value集合中的u_mid的唯一个数
 * @Date 2020/1/12 17:10
 */
public class NewMemberReducer extends Reducer<StatsUserDimension, TimeOutputValue, StatsUserDimension, MapWritableValue> {
    private Set<String> unique = new HashSet<String>();
    private MapWritableValue outputValue = new MapWritableValue();
    private MapWritable map = new MapWritable();

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values, Context context) throws IOException, InterruptedException {
        for (TimeOutputValue value : values) {
            this.unique.add(value.getId());
        }

        // 输出memberid
        this.outputValue.setKpi(KpiType.INSERT_MEMBER_INFO);
        for (String id : this.unique) {
            this.map.put(new IntWritable(-1), new Text(id));
            this.outputValue.setValue(this.map);
            context.write(key, this.outputValue);
        }

        // value指定
        this.map.put(new IntWritable(-1), new IntWritable(this.unique.size()));
        this.outputValue.setValue(this.map);
        // kpi指定
        this.outputValue.setKpi(KpiType.valueOfName(key.getStatsCommon().getKpi().getKpiName()));
        context.write(key, this.outputValue);
    }
}
