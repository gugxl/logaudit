package com.gugu.transformer.model.dim;

import com.gugu.transformer.model.dim.base.BaseDimension;
import com.gugu.transformer.model.dim.base.InboundDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author gugu
 * @Classname StatsInboundDimension
 * @Description TODO
 * @Date 2020/1/10 21:41
 */
public class StatsInboundDimension extends StatsDimension {
    private StatsCommonDimension statsCommon = new StatsCommonDimension();
    private InboundDimension inbound = new InboundDimension();
    /**
     * 根据已有的实例对象克隆一个对象
     *
     * @param dimension
     * @return
     */
    public static StatsInboundDimension clone(StatsInboundDimension dimension) {
        return new StatsInboundDimension(StatsCommonDimension.clone(dimension.statsCommon), new InboundDimension(dimension.inbound));
    }
    public StatsInboundDimension() {
        super();
    }

    public StatsInboundDimension(StatsCommonDimension statsCommon, InboundDimension inbound) {
        super();
        this.statsCommon = statsCommon;
        this.inbound = inbound;
    }

    public StatsCommonDimension getStatsCommon() {
        return statsCommon;
    }

    public void setStatsCommon(StatsCommonDimension statsCommon) {
        this.statsCommon = statsCommon;
    }

    public InboundDimension getInbound() {
        return inbound;
    }

    public void setInbound(InboundDimension inbound) {
        this.inbound = inbound;
    }
    @Override
    public int compareTo(BaseDimension o) {
        StatsInboundDimension other = (StatsInboundDimension) o;
        int tmp = this.statsCommon.compareTo(other.statsCommon);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.inbound.compareTo(other.inbound);
        return tmp;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.statsCommon.write(out);
        this.inbound.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.statsCommon.readFields(in);
        this.inbound.readFields(in);
    }
}
