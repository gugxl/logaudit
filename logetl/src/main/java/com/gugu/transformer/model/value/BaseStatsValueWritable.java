package com.gugu.transformer.model.value;

import com.gugu.common.KpiType;
import org.apache.hadoop.io.Writable;

/**
 * @author gugu
 * @Classname BaseStatsValueWritable
 * @Description TODO
 * @Date 2020/1/5 9:08
 */
public abstract class BaseStatsValueWritable implements Writable {
    public abstract KpiType getKpi();
}
