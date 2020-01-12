package com.gugu.transformer.mr.inbound;

import com.gugu.common.KpiType;
import com.gugu.transformer.model.dim.StatsInboundDimension;
import com.gugu.transformer.model.dim.base.KpiDimension;
import com.gugu.transformer.model.value.map.TextsOutputValue;
import com.gugu.transformer.mr.TransformerBaseMapper;
import com.gugu.transformer.serice.impl.InboundDimensionService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author gugu
 * @Classname InboundMapper
 * @Description 统计inbound相关的活跃用户和总会话个数的一个mapper类<br/>
 * 输入: platform、servertime、referrer url、uuid、sid<br/>
 * @Date 2020/1/10 21:39
 */
public class InboundMapper extends TransformerBaseMapper<StatsInboundDimension, TextsOutputValue> {
    private static final Logger logger = Logger.getLogger(InboundMapper.class);
    private StatsInboundDimension statsInboundDimension = new StatsInboundDimension();
    private TextsOutputValue outputValue = new TextsOutputValue();
    private KpiDimension inboundKpiDimension = new KpiDimension(KpiType.INBOUND.name);
    private Map<String, Integer> inbounds = null;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        try {
            // 获取inbound相关数据
            this.inbounds = InboundDimensionService.getInboundByType(context.getConfiguration(), 0);
        } catch (SQLException e) {
            logger.error("获取外链id出现数据库异常", e);
            throw new IOException("出现异常", e);
        }
    }
}
