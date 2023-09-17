package cn.thinkinginjava.data.service.impl;

import cn.thinkinginjava.data.mapper.DatasetMapper;
import cn.thinkinginjava.data.model.dto.DatasetDTO;
import cn.thinkinginjava.data.model.entity.Dataset;
import cn.thinkinginjava.data.model.entity.Datasource;
import cn.thinkinginjava.data.model.vo.DatasetVO;
import cn.thinkinginjava.data.service.DataSourceService;
import cn.thinkinginjava.data.service.DatasetService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class DatasetServiceImpl extends ServiceImpl<DatasetMapper, Dataset> implements DatasetService {

    @Resource
    private DataSourceService dataSourceService;

    @Override
    public IPage<DatasetVO> listByPage(DatasetDTO datasetDTO) {
        LambdaQueryWrapper<Dataset> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dataset::getValid, 1);
        queryWrapper.orderByDesc(Dataset::getUpdateAt);
        return page(new Page<>(datasetDTO.getCurrentPage(),
                datasetDTO.getPageSize()), queryWrapper).convert(dataset -> {
            DatasetVO datasetVO = new DatasetVO();
            BeanUtils.copyProperties(dataset, datasetVO);
            Datasource datasource = dataSourceService.getById(dataset.getDatasourceId());
            datasetVO.setDatasourceName(datasource.getName());
            datasetVO.setCreateTime(DateFormatUtils.format(dataset.getCreateAt(), "yyyy-MM-dd HH:mm:ss"));
            datasetVO.setUpdateTime(DateFormatUtils.format(dataset.getUpdateAt(), "yyyy-MM-dd HH:mm:ss"));
            return datasetVO;
        });
    }
}
