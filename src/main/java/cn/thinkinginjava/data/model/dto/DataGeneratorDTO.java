package cn.thinkinginjava.data.model.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DataGeneratorDTO {

    /**
     * 指定生成的mock数据数量
     */
    private Integer generateNum = 50;

    /**
     * 指定表中字段取值范围
     */
    private Map<String, List<Object>> defaultTableValue = new HashMap<>();

    /**
     * DDL列表
     */
    private List<String> ddlList;

    /**
     * 查询SQL
     */
    private String querySql;

    private String id;

    private Map<String,String> extWhereMap = new HashMap<>();

    private Map<String,String> ddlMap = new HashMap<>();
}
