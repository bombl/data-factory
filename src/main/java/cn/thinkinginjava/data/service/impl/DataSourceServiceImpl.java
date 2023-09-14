package cn.thinkinginjava.data.service.impl;

import cn.thinkinginjava.data.model.entity.Datasource;
import cn.thinkinginjava.data.model.vo.DatasourceVO;
import cn.thinkinginjava.data.service.DataSourceService;
import cn.thinkinginjava.data.utils.DataSourceHolder;
import cn.thinkinginjava.data.mapper.DatasourceMapper;
import cn.thinkinginjava.data.model.dto.SaveDataDTO;
import cn.thinkinginjava.data.model.dto.DatasourceDTO;
import cn.thinkinginjava.data.utils.DdlUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DataSourceServiceImpl extends ServiceImpl<DatasourceMapper, Datasource> implements DataSourceService {

    @Override
    public Boolean testConnection(DatasourceDTO dataSourceDTO) {
        DruidDataSource druidDataSource = DataSourceHolder.getDataSource(dataSourceDTO);
        DruidPooledConnection connection = null;
        try {
            connection = druidDataSource.getConnection();
            String catalog = connection.getCatalog();
            log.info("数据库连接成功：{}", catalog);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.info("数据库连接失败");
            return Boolean.FALSE;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.info("关闭连接异常");
                }
            }
            if (druidDataSource != null) {
                druidDataSource.close();
            }
        }
    }

    @Override
    public List<String> getDdl(String id, List<String> tableNames) {
        List<String> ddlList = new ArrayList<>();
        for (String tableName : tableNames) {
            Datasource datasource = getById(id);
            DatasourceDTO datasourceDTO = new DatasourceDTO();
            BeanUtils.copyProperties(datasource, datasourceDTO);
            DruidDataSource dataSource = DataSourceHolder.getDataSource(datasourceDTO);
            try (Connection connection = dataSource.getPooledConnection().getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                String databaseProductName = metaData.getDatabaseProductName().toLowerCase();
                String sql = "";
                if (databaseProductName.contains("mysql")) {
                    sql = "SHOW CREATE TABLE " + tableName;
                } else if (databaseProductName.contains("oracle")) {
                    sql = "SELECT dbms_metadata.get_ddl('TABLE','" + tableName + "' ) FROM dual";
                }
                try {
                    ddlList.add(DdlUtil.rewriteDdl(executeQuerySql(connection, sql)));
                } catch (Exception e) {
                    if (databaseProductName.contains("oracle")) {
                        sql = "SELECT T.TABLE_OWNER FROM ALL_SYNONYMS  T WHERE T.SYNONYM_NAME = '" + tableName + "'";
                        String schema = executeQuerySql(connection, sql);
                        sql = "SELECT dbms_metadata.get_ddl('TABLE','" + tableName + "','" + schema + "') FROM dual";
                        ddlList.add(DdlUtil.rewriteDdl(executeQuerySql(connection, sql)));
                    }
                }
            } catch (SQLException ignored) {
            }
        }
        return ddlList;
    }

    @Override
    public void saveData(SaveDataDTO saveDataDTO) {
        DruidDataSource dataSource = DataSourceHolder.getDataSource(saveDataDTO.getId());
        try (Connection connection = dataSource.getPooledConnection().getConnection()) {
            batchExecuteUpdateSql(connection, saveDataDTO.getSqlList());
        } catch (SQLException ignored) {
        }
    }

    @Override
    public IPage<DatasourceVO> listByPage(DatasourceDTO datasourceDTO) {
        LambdaQueryWrapper<Datasource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Datasource::getValid, 1);
        queryWrapper.orderByDesc(Datasource::getUpdateAt);
        return page(new Page<>(datasourceDTO.getCurrentPage(),
                datasourceDTO.getPageSize()), queryWrapper).convert(datasource -> {
            DatasourceVO datasourceVO = new DatasourceVO();
            BeanUtils.copyProperties(datasource, datasourceVO);
            datasourceVO.setCreateTime(DateFormatUtils.format(datasource.getCreateAt(), "yyyy-MM-dd HH:mm:ss"));
            datasourceVO.setUpdateTime(DateFormatUtils.format(datasource.getUpdateAt(), "yyyy-MM-dd HH:mm:ss"));
            return datasourceVO;
        });
    }

    private String executeQuerySql(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(1);
                }
            }
        }
        return null;
    }

    private void batchExecuteUpdateSql(Connection connection, List<String> sqls) {
        try {
            for (String sql : sqls) {
                try (PreparedStatement statement = connection.prepareStatement(sql.replace(";", ""))) {
                    statement.executeUpdate();
                    connection.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("Sql执行异常.");
                }
            }
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
