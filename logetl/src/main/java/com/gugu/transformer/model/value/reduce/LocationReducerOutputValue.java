package com.gugu.transformer.model.value.reduce;

import com.gugu.common.KpiType;
import com.gugu.transformer.model.value.BaseStatsValueWritable;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author gugu
 * @Classname LocationReducerOutputValue
 * @Description 自定义location统计reducer的输出value类
 * @Date 2020/1/10 19:52
 */
public class LocationReducerOutputValue extends BaseStatsValueWritable {
    private KpiType kpi;
    private int uvs; // 活跃用户数
    private int visits; // 会话个数
    private int bounceNumber; // 跳出会话个数

    public void setKpi(KpiType kpi) {
        this.kpi = kpi;
    }

    public int getUvs() {
        return uvs;
    }

    public void setUvs(int uvs) {
        this.uvs = uvs;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public int getBounceNumber() {
        return bounceNumber;
    }

    public void setBounceNumber(int bounceNumber) {
        this.bounceNumber = bounceNumber;
    }

    @Override
    public KpiType getKpi() {
        return kpi;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(uvs);
        out.writeInt(visits);
        out.writeInt(bounceNumber);
        WritableUtils.writeEnum(out, this.kpi);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.uvs = in.readInt();
        this.visits = in.readInt();
        this.bounceNumber = in.readInt();
        this.kpi = WritableUtils.readEnum(in, KpiType.class);
    }
}
