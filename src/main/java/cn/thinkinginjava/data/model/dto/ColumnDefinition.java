
package cn.thinkinginjava.data.model.dto;

import lombok.Data;
import net.sf.jsqlparser.statement.create.table.ColDataType;

import java.io.Serializable;
import java.util.List;

/**
 * Globally used definition class for columns.
 */
@Data
public class ColumnDefinition implements Serializable {

    private String columnName;
    private ColDataType colDataType;
    private List<String> columnSpecs;
    private String option;
    private String value;

}
