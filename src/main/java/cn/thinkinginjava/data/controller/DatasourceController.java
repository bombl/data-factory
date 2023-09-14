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

import cn.thinkinginjava.data.model.entity.Datasource;
import cn.thinkinginjava.data.model.vo.DatasourceVO;
import cn.thinkinginjava.data.service.DataSourceService;
import cn.thinkinginjava.data.utils.DataSourceHolder;
import cn.thinkinginjava.data.model.dto.Result;
import cn.thinkinginjava.data.model.dto.DatasourceDTO;
import cn.thinkinginjava.data.utils.UUIDUtils;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DatasourceController {

    @Resource
    private DataSourceService dataSourceService;


    @RequestMapping("/saveOrUpdate")
    @ResponseBody
    public Result<Void> saveOrUpdate(@Valid @RequestBody DatasourceDTO datasourceDTO) {
        if (StringUtils.isNotBlank(datasourceDTO.getId())) {
            Datasource datasource = dataSourceService.getById(datasourceDTO.getId());
            if (datasource != null && datasource.getValid() != 0) {
                Datasource updateDatasource = new Datasource();
                BeanUtils.copyProperties(datasourceDTO,updateDatasource);
                updateDatasource.setUpdateAt(new Date());
                dataSourceService.updateById(updateDatasource);
                return Result.successful();
            }
        }
        Datasource saveDatasource = new Datasource();
        BeanUtils.copyProperties(datasourceDTO,saveDatasource);
        saveDatasource.setId(UUIDUtils.getUuid());
        saveDatasource.setEnabled(1);
        saveDatasource.setValid(1);
        saveDatasource.setCreateAt(new Date());
        saveDatasource.setUpdateAt(new Date());
        dataSourceService.save(saveDatasource);
        return Result.successful();
    }


    @RequestMapping("/delete")
    @ResponseBody
    public Result<Void> delete(@Valid @RequestBody DatasourceDTO datasourceDTO) {
        if (StringUtils.isNotBlank(datasourceDTO.getId())) {
            Datasource datasource = dataSourceService.getById(datasourceDTO.getId());
            if (datasource != null) {
                DruidDataSource dataSource = DataSourceHolder.getDataSource(datasourceDTO.getId());
                try {
                    dataSource.close();
                } catch (Exception ignored) {
                }
                dataSourceService.removeById(datasourceDTO.getId());
                return Result.successful();
            }
        }
        return Result.successful();
    }

    @PostMapping("/test")
    @ResponseBody
    public Result<Boolean> test(@RequestBody DatasourceDTO dataSourceDTO) {
        Boolean result = dataSourceService.testConnection(dataSourceDTO);
        return Result.successful(result);
    }

    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(@RequestBody DatasourceDTO datasourceDTO) {
        IPage<DatasourceVO> page = dataSourceService.listByPage(datasourceDTO);
        Map<String, Object> map = new HashMap<>();
        map.put("recordsTotal", page.getRecords().size());
        map.put("recordsFiltered", page.getRecords().size());
        map.put("data", page.getRecords());
        return map;
    }
}
