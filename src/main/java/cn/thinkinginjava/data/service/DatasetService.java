package cn.thinkinginjava.data.service;

import cn.thinkinginjava.data.model.dto.DatasetDTO;
import cn.thinkinginjava.data.model.entity.Dataset;
import cn.thinkinginjava.data.model.vo.DatasetVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DatasetService extends IService<Dataset> {

    IPage<DatasetVO> listByPage(DatasetDTO datasetDTO);
}
