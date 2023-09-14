package cn.thinkinginjava.data.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipField {
    private String tableName;
    private String column;
}