package cn.thinkinginjava.data.service.impl;

import cn.thinkinginjava.data.model.dto.DatasourceDTO;
import cn.thinkinginjava.data.model.entity.Dataset;
import cn.thinkinginjava.data.model.entity.Datasource;
import cn.thinkinginjava.data.service.DataSourceService;
import cn.thinkinginjava.data.service.DatasetService;
import cn.thinkinginjava.data.utils.DataSourceHolder;
import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
public class DatasetManager {

    @Resource
    private DataSourceService dataSourceService;

    @Resource
    private DatasetService datasetService;

//    @Cacheable(value = "default", key = "#datasetId")
    public List<Object> getDataList(String datasetId) {
        List<Object> result = Lists.newArrayList();
        Dataset dataset = datasetService.getById(datasetId);
        if (dataset == null) {
            return result;
        }
        String datasourceId = dataset.getDatasourceId();
        Datasource datasource = dataSourceService.getById(datasourceId);
        DatasourceDTO datasourceDTO = new DatasourceDTO();
        BeanUtils.copyProperties(datasource, datasourceDTO);
        DruidDataSource dataSource = DataSourceHolder.getDataSource(datasourceDTO);
        try (Connection connection = dataSource.getPooledConnection().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(dataset.getScript().replace(";", ""))) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result.add(resultSet.getObject(1));
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.info("Sql执行异常.");
            }
        } catch (SQLException ignored) {
        }
        return result;
    }
}
