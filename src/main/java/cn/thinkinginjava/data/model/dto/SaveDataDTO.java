package cn.thinkinginjava.data.model.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SaveDataDTO {

    /**
     * 数据库ID
     */
    private String id;

    /**
     * SQL语句
     */
    private List<String> sqlList;
}
