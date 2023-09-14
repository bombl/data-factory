package cn.thinkinginjava.data.service;

import cn.thinkinginjava.data.model.dto.SaveDataDTO;
import cn.thinkinginjava.data.model.dto.DatasourceDTO;
import cn.thinkinginjava.data.model.entity.Datasource;
import cn.thinkinginjava.data.model.vo.DatasourceVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DataSourceService extends IService<Datasource> {

    Boolean testConnection(DatasourceDTO dataSourceDTO);

    List<String> getDdl(String id, List<String> tableNames);

    void saveData(SaveDataDTO saveDataDTO);

    IPage<DatasourceVO> listByPage(DatasourceDTO datasourceDTO);
}
