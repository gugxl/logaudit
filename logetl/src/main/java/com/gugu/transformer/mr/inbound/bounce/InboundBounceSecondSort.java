package com.gugu.transformer.mr.inbound.bounce;

import com.gugu.transformer.model.dim.StatsCommonDimension;
import com.gugu.transformer.model.dim.StatsInboundBounceDimension;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapred.lib.HashPartitioner;
import org.apache.hadoop.mapreduce.Partitioner;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author gugu
 * @Classname InboundBounceSecondSort
 * @Description 自定义的二次排序使用到的类
 * @Date 2020/1/12 16:24
 */
public class InboundBounceSecondSort {
    public static class InboundBounceGroupingComparator extends WritableComparator {
        public InboundBounceGroupingComparator() {
            super(StatsInboundBounceDimension.class, true);
        }


        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            StatsInboundBounceDimension key1 = (StatsInboundBounceDimension) a;
            StatsInboundBounceDimension key2 = (StatsInboundBounceDimension) b;
            return key1.getStatsCommon().compareTo(key2.getStatsCommon());
        }

    }

    public static class InboundBouncePartitioner extends Partitioner<StatsInboundBounceDimension, IntWritable> {
        private HashPartitioner<StatsCommonDimension, IntWritable> partitioner = new HashPartitioner<StatsCommonDimension, IntWritable>();

        @Override
        public int getPartition(StatsInboundBounceDimension dimension, IntWritable intWritable, int numPartitions) {
            return this.partitioner.getPartition(dimension.getStatsCommon(), intWritable, numPartitions);

        }
    }
}
