package com.gugu.transformer.model.value.reduce;

import com.gugu.common.KpiType;
import com.gugu.transformer.model.value.BaseStatsValueWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author gugu
 * @Classname MapWritableValue
 * @Description TODO
 * @Date 2020/1/5 9:12
 */
public class MapWritableValue extends BaseStatsValueWritable {
    private MapWritable value = new MapWritable();//即将插入数据库表中的一行记录
    private KpiType kpi;
    public MapWritableValue() {
    }

    public MapWritableValue(MapWritable value, KpiType kpi) {
        this.value = value;
        this.kpi = kpi;
    }
    public MapWritable getValue() {
        return value;
    }

    public void setValue(MapWritable value) {
        this.value = value;
    }

    public void setKpi(KpiType kpi) {
        this.kpi = kpi;
    }
    @Override
    public KpiType getKpi() {
        return this.kpi;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.value.write(out);
        WritableUtils.writeEnum(out, this.kpi);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.value.readFields(in);
        this.kpi = WritableUtils.readEnum(in, KpiType.class);
    }
}
