package com.gugu.transformer.serice;

import com.gugu.transformer.model.dim.base.BaseDimension;

import java.io.IOException;

//提供专门操作（从关系型数据库中查询、插入）dimension表的接口
public interface IDimensionConverter {
    /**
     * @Description 根据dimension的value值获取id<br/>
     * 如果数据库中有，那么直接返回。如果没有，那么进行插入后返回新的id值
     * @params 
     * @param dimension
     * @return int
     * @auther gugu
     */
    
    public int getDimensionIdByValue(BaseDimension dimension) throws IOException;
}
