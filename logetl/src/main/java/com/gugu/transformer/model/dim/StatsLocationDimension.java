package com.gugu.transformer.model.dim;

import com.gugu.transformer.model.dim.base.BaseDimension;
import com.gugu.transformer.model.dim.base.LocationDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author gugu
 * @Classname StatsLocationDimension
 * @Description 统计location相关的维度类
 * @Date 2020/1/10 19:01
 */
public class StatsLocationDimension extends StatsDimension {
    private StatsCommonDimension statsCommon = new StatsCommonDimension();
    private LocationDimension location = new LocationDimension();
    public static StatsLocationDimension clone(StatsLocationDimension dimension){
        StatsLocationDimension statsLocationDimension = new StatsLocationDimension();
        statsLocationDimension.statsCommon = StatsCommonDimension.clone(dimension.statsCommon);
        statsLocationDimension.location = LocationDimension.newInstance(dimension.location.getCountry(), dimension.location.getProvince(), dimension.location.getCity());
        statsLocationDimension.location.setId(dimension.location.getId());
        return statsLocationDimension;
    }
    public StatsLocationDimension() {
        super();
    }

    public StatsLocationDimension(StatsCommonDimension statsCommon, LocationDimension location) {
        super();
        this.statsCommon = statsCommon;
        this.location = location;
    }

    public StatsCommonDimension getStatsCommon() {
        return statsCommon;
    }

    public void setStatsCommon(StatsCommonDimension statsCommon) {
        this.statsCommon = statsCommon;
    }

    public LocationDimension getLocation() {
        return location;
    }

    public void setLocation(LocationDimension location) {
        this.location = location;
    }
    @Override
    public int compareTo(BaseDimension o) {
        StatsLocationDimension statsLocationDimension = (StatsLocationDimension) o;
        int tmp = this.statsCommon.compareTo(statsLocationDimension.statsCommon);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.location.compareTo(statsLocationDimension.location);
        return tmp;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.statsCommon.write(out);
        this.location.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.statsCommon.readFields(in);
        this.location.readFields(in);
    }
}
