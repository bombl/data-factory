package cn.thinkinginjava.data.utils;

import cn.thinkinginjava.data.model.dto.DatasourceDTO;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class DataSourceHolder {

    public final static Map<String, DruidDataSource> map = new HashMap<>();

    public static DruidDataSource getDataSource(DatasourceDTO dataSourceDTO) {
        if (StringUtils.isBlank(dataSourceDTO.getId())) {
            return DataSourceUtil.getDataSource(dataSourceDTO);
        }
        DruidDataSource druidDataSource = map.get(dataSourceDTO.getId());
        if (druidDataSource == null) {
            druidDataSource = DataSourceUtil.getDataSource(dataSourceDTO);
            map.put(dataSourceDTO.getId(), druidDataSource);
        }
        return map.get(dataSourceDTO.getId());
    }

    public static DruidDataSource getDataSource(String id) {
        return map.get(id);
    }
}
