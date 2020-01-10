package com.gugu.transformer.model.value.reduce;

import com.gugu.common.KpiType;
import com.gugu.transformer.model.value.BaseStatsValueWritable;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author gugu
 * @Classname InboundBounceReduceValue
 * @Description TODO
 * @Date 2020/1/10 19:22
 */
public class InboundBounceReduceValue extends BaseStatsValueWritable {
    private KpiType kpi;
    private int bounceNumber;
    public InboundBounceReduceValue() {
        super();
    }

    public InboundBounceReduceValue(int bounceNumber) {
        super();
        this.bounceNumber = bounceNumber;
    }

    public int getBounceNumber() {
        return bounceNumber;
    }

    public void setBounceNumber(int bounceNumber) {
        this.bounceNumber = bounceNumber;
    }

    public void setKpi(KpiType kpi) {
        this.kpi = kpi;
    }
    /**
     * @Description 自增1
     * @params
     * @return void
     * @auther gugu
     */

    public void incrBounceNum(){
        this.bounceNumber++;
    }

    @Override
    public KpiType getKpi() {
        return this.kpi;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.bounceNumber);
        WritableUtils.writeEnum(out, this.kpi);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.bounceNumber = in.readInt();
        this.kpi = WritableUtils.readEnum(in, KpiType.class);
    }
}
