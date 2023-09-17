/**
 * Mockit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Mockit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Mockit. If not, see <http://www.gnu.org/licenses/>.
 */

package cn.thinkinginjava.data.controller;

import cn.thinkinginjava.data.model.dto.DatasetDTO;
import cn.thinkinginjava.data.model.dto.DatasourceDTO;
import cn.thinkinginjava.data.model.dto.Result;
import cn.thinkinginjava.data.model.entity.Dataset;
import cn.thinkinginjava.data.model.entity.Datasource;
import cn.thinkinginjava.data.model.vo.DatasetVO;
import cn.thinkinginjava.data.model.vo.DatasourceVO;
import cn.thinkinginjava.data.service.DatasetService;
import cn.thinkinginjava.data.utils.DataSourceHolder;
import cn.thinkinginjava.data.utils.UUIDUtils;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DatasetController {

    @Resource
    private DatasetService datasetService;


    @RequestMapping("/dataset/saveOrUpdate")
    @ResponseBody
    public Result<Void> saveOrUpdate(@Valid @RequestBody DatasetDTO datasetDTO) {
        if (StringUtils.isNotBlank(datasetDTO.getId())) {
            Dataset dataset = datasetService.getById(datasetDTO.getId());
            if (dataset != null && dataset.getValid() != 0) {
                Dataset updateDataset = new Dataset();
                BeanUtils.copyProperties(datasetDTO,updateDataset);
                updateDataset.setUpdateAt(new Date());
                datasetService.updateById(updateDataset);
                return Result.successful();
            }
        }
        Dataset saveDataset = new Dataset();
        BeanUtils.copyProperties(datasetDTO,saveDataset);
        saveDataset.setId(UUIDUtils.getUuid());
        saveDataset.setEnabled(1);
        saveDataset.setValid(1);
        saveDataset.setCreateAt(new Date());
        saveDataset.setUpdateAt(new Date());
        datasetService.save(saveDataset);
        return Result.successful();
    }


    @RequestMapping("/dataset/delete")
    @ResponseBody
    public Result<Void> delete(@Valid @RequestBody DatasetDTO datasetDTO) {
        if (StringUtils.isNotBlank(datasetDTO.getId())) {
            datasetService.removeById(datasetDTO.getId());
            return Result.successful();
        }
        return Result.successful();
    }

    @RequestMapping("/dataset/list")
    @ResponseBody
    public Map<String, Object> list(@RequestBody DatasetDTO datasetDTO) {
        IPage<DatasetVO> page = datasetService.listByPage(datasetDTO);
        Map<String, Object> map = new HashMap<>();
        map.put("recordsTotal", page.getRecords().size());
        map.put("recordsFiltered", page.getRecords().size());
        map.put("data", page.getRecords());
        return map;
    }
}
