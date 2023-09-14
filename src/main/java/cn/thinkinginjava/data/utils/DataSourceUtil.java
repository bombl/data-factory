package cn.thinkinginjava.data.utils;

import cn.thinkinginjava.data.model.dto.DatasourceDTO;
import com.alibaba.druid.pool.DruidDataSource;

public class DataSourceUtil {

    public static DruidDataSource getDataSource(DatasourceDTO dataSourceDTO) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(dataSourceDTO.getLink());
        dataSource.setUsername(dataSourceDTO.getUsername());
        dataSource.setPassword(dataSourceDTO.getPassword());
        dataSource.setMinIdle(5);      // 最小空闲连接数
        dataSource.setMaxActive(20);   // 最大活跃连接数
        dataSource.setInitialSize(5); // 初始连接数
        dataSource.setMaxWait(60000);  // 最大等待时间，单位毫秒
        String dbType = dataSource.getDbType();
        System.out.println(dbType);
        if (dataSourceDTO.getLink().startsWith("jdbc:oracle")) {
            dataSource.setValidationQuery("SELECT 1 FROM DUAL"); // 用于检测连接是否有效的 SQL 查询
        } else {
            dataSource.setValidationQuery("SELECT 1"); // 用于检测连接是否有效的 SQL 查询
        }
        dataSource.setTestOnBorrow(true);  // 借用连接时是否进行验证
        dataSource.setUseGlobalDataSourceStat(true);  // 打开全局监控统计
        dataSource.setLogAbandoned(true);            // 打开连接泄漏检测
        dataSource.setRemoveAbandoned(true);         // 自动关闭泄漏连接
        dataSource.setRemoveAbandonedTimeout(180);  // 泄漏连接的检测周期，单位秒
        return dataSource;
    }
}
