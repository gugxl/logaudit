package com.gugu.transformer.model.value.map;

import com.gugu.common.KpiType;
import com.gugu.transformer.model.value.BaseStatsValueWritable;
import org.datanucleus.util.StringUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author gugu
 * @Classname TextsOutputValue
 * @Description 定义一系列的字符串输出类
 * @Date 2020/1/10 19:11
 */
public class TextsOutputValue extends BaseStatsValueWritable {
    private KpiType kpiType;
    private String uuid; // 用户唯一标识符
    private String sid; // 会话id
    public TextsOutputValue() {
        super();
    }

    public TextsOutputValue(String uuid, String sid) {
        super();
        this.uuid = uuid;
        this.sid = sid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setKpiType(KpiType kpiType) {
        this.kpiType = kpiType;
    }
    @Override
    public KpiType getKpi() {
        return this.kpiType;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.internalWriteString(out, this.uuid);
        this.internalWriteString(out, this.sid);
    }
    private void internalWriteString(DataOutput out,String value) throws IOException {
        if (StringUtils.isEmpty(value)){
            out.writeBoolean(false);
        }else {
            out.writeBoolean(true);
            out.writeUTF(value);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.uuid = internalReadString(in);
        this.sid = internalReadString(in);
    }

    private String internalReadString(DataInput in) throws IOException {
        return in.readBoolean() ? in.readUTF() : null;
    }

}
