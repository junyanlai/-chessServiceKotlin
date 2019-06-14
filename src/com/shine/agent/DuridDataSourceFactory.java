package com.shine.agent;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

public class DuridDataSourceFactory extends UnpooledDataSourceFactory {
    public DuridDataSourceFactory() {
        dataSource = new DruidDataSource();
    }

}
