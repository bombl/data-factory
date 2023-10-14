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

import cn.thinkinginjava.data.core.DataGenerator;
import cn.thinkinginjava.data.core.SQLParser;
import cn.thinkinginjava.data.model.dto.DataGeneratorDTO;
import cn.thinkinginjava.data.model.dto.Result;
import cn.thinkinginjava.data.model.dto.SaveDataDTO;
import cn.thinkinginjava.data.service.DataSourceService;
import cn.thinkinginjava.data.utils.DdlUtil;
import com.google.common.collect.Lists;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class DataController {

    @Resource
    private DataSourceService dataSourceService;

    /**
     * Controller method for handling the index page
     *
     * @param model model
     * @return modelAndView
     */
    @RequestMapping("/")
    public ModelAndView index(Model model) {
        return new ModelAndView("forward:/index");
    }

    /**
     * Controller method for handling the index page
     *
     * @param model model
     * @return index
     */
    @RequestMapping("/index")
    public String index0(Model model) {
        return "index";
    }

    @RequestMapping("/datasource")
    public String datasource(Model model) {
        return "datasource";
    }

    @RequestMapping("/dataset")
    public String dataset(Model model) {
        return "dataset";
    }

    @PostMapping("/generateData")
    @ResponseBody
    public Result<Map<String, List<String>>> generateData(@RequestBody DataGeneratorDTO dataGeneratorDTO) throws JSQLParserException {
        List<String> tableNames = new SQLParser(dataGeneratorDTO.getQuerySql()).parse();
        Map<String, String> ddlMap = dataGeneratorDTO.getDdlMap();
        if (MapUtils.isNotEmpty(ddlMap)) {
            tableNames.removeAll(ddlMap.keySet());
        }
        List<String> ddlList = dataSourceService.getDdl(dataGeneratorDTO.getId(), tableNames);
        if (CollectionUtils.isNotEmpty(ddlMap.values())) {
            for (String ddl : ddlMap.values()) {
                ddlList.add(DdlUtil.rewriteDdl(ddl));
            }
        }
        dataGeneratorDTO.setDdlList(ddlList);
        DataGenerator dataGenerator = new DataGenerator(dataGeneratorDTO);
        Map<String, List<String>> result = dataGenerator.generateData();
        return Result.successful(result);
    }

    @PostMapping("/saveData")
    @ResponseBody
    public Result<Void> saveData(@RequestBody SaveDataDTO saveDataDTO) throws JSQLParserException {
        dataSourceService.saveData(saveDataDTO);
        return Result.successful();
    }

    @PostMapping("/queryTableNames")
    @ResponseBody
    public Result<List<String>> queryTableNames(@RequestBody DataGeneratorDTO dataGeneratorDTO) {
        List<String> result = new SQLParser(dataGeneratorDTO.getQuerySql()).parse();
        return Result.successful(result);
    }

    @PostMapping("/parseSqlDefine")
    @ResponseBody
    public Result<Map<String, List<cn.thinkinginjava.data.model.dto.ColumnDefinition>>> parseSqlDefine(@RequestBody DataGeneratorDTO dataGeneratorDTO) throws JSQLParserException {
        Map<String, List<cn.thinkinginjava.data.model.dto.ColumnDefinition>> result = new HashMap<>();
        List<String> tableNames = new SQLParser(dataGeneratorDTO.getQuerySql()).parse();
        Map<String, String> ddlMap = dataGeneratorDTO.getDdlMap();
        if (MapUtils.isNotEmpty(ddlMap)) {
            tableNames.removeAll(ddlMap.keySet());
        }
        List<String> ddlList = dataSourceService.getDdl(dataGeneratorDTO.getId(), tableNames);
        if (CollectionUtils.isNotEmpty(ddlMap.values())) {
            for (String ddl : ddlMap.values()) {
                ddlList.add(DdlUtil.rewriteDdl(ddl));
            }
        }
        if (CollectionUtils.isEmpty(ddlList)) {
            for (String tableName : tableNames) {
                result.put(tableName,Lists.newArrayList());
            }
            return Result.successful(result);
        }
        dataGeneratorDTO.setDdlList(ddlList);
        DataGenerator dataGenerator = new DataGenerator(dataGeneratorDTO);
        Map<String, String> whereCondition = dataGenerator.parseWhereCondition();
        for (String ddl : ddlList) {
            CreateTable createTable = (CreateTable) CCJSqlParserUtil.parse(ddl);
            List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
            List<cn.thinkinginjava.data.model.dto.ColumnDefinition> definitions = new ArrayList<>();
            for (ColumnDefinition columnDefinition : columnDefinitions) {
                cn.thinkinginjava.data.model.dto.ColumnDefinition definition = new cn.thinkinginjava.data.model.dto.ColumnDefinition();
                BeanUtils.copyProperties(columnDefinition, definition);
                String valueStr = whereCondition.get(createTable.getTable().getName().toUpperCase(Locale.ROOT) + "-" + columnDefinition.getColumnName().toUpperCase(Locale.ROOT));
                setOptionValue(definition, valueStr);
                definitions.add(definition);
            }
            result.put(createTable.getTable().getName().toUpperCase(Locale.ROOT), definitions);
        }
        return Result.successful(result);
    }

    private void setOptionValue(cn.thinkinginjava.data.model.dto.ColumnDefinition definition, String valueStr) {
        if (StringUtils.isNotBlank(valueStr)) {
            Pattern inPattern = Pattern.compile("IN\\((.*?)\\)");
            Matcher inMatcher = inPattern.matcher(valueStr);
            Pattern notInPattern = Pattern.compile("IN\\((.*?)\\)");
            Matcher notInMatcher = notInPattern.matcher(valueStr);
            if (inMatcher.find()) {
                String valuesPart = inMatcher.group(1);
                definition.setOption("IN");
                definition.setValue(valuesPart.replaceAll("'", ""));
            } else if (notInMatcher.find()) {
                String valuesPart = notInMatcher.group(1);
                definition.setOption("NOT IN");
                definition.setValue(valuesPart.replaceAll("'", ""));
            } else if (valueStr.startsWith("LIKE")) {
                definition.setOption("LIKE");
                definition.setValue(valueStr.replaceFirst("LIKE ", ""));
            } else if (valueStr.startsWith("!=")) {
                definition.setOption("!=");
                definition.setValue(valueStr.replaceFirst("!=", ""));
            } else if (valueStr.startsWith(">") || valueStr.startsWith("<")) {
                definition.setOption(valueStr.charAt(0) + "");
                definition.setValue(valueStr.substring(1));
            } else {
                definition.setOption("=");
                definition.setValue(valueStr);
            }
        }
    }
}
