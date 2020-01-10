package com.gugu.transformer.hive;

import com.gugu.common.DateEnum;
import com.gugu.transformer.model.dim.base.DateDimension;
import com.gugu.transformer.serice.IDimensionConverter;
import com.gugu.transformer.serice.impl.DimensionConverterImpl;
import com.gugu.util.TimeUtil;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * @author gugu
 * @Classname DateDimensionUDF
 * @Description TODO
 * @Date 2020/1/7 14:48
 */
public class DateDimensionUDF extends UDF {
    private IDimensionConverter converter = new DimensionConverterImpl();
    public IntWritable evaluate(Text day){
        DateDimension dimension = DateDimension.buildDate(TimeUtil.parseString2Long(day.toString()), DateEnum.DAY);
        int id = 0;
        try {
            id = this.converter.getDimensionIdByValue(dimension);
        } catch (IOException e) {
            throw new RuntimeException("获取id异常");
        }
    }
}
