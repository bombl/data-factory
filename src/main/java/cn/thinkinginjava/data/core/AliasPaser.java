package cn.thinkinginjava.data.core;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 别名解析器
 */
public class AliasPaser {

    private final PlainSelect plainSelect;

    public AliasPaser(PlainSelect plainSelect) {
        this.plainSelect = plainSelect;
    }

    public Map<String, String> parseAlias() {
        Map<String, String> aliasMap = new HashMap<>();
        List<Join> joins = plainSelect.getJoins();
        // 获取表名和表别名
        Table table = (Table) plainSelect.getFromItem();
        aliasMap.put(table.getAlias().getName(), table.getName());
        if (joins == null) {
            return aliasMap;
        }
        for (Join join : joins) {
            FromItem rightItem = join.getRightItem();
            if (rightItem instanceof Table) {
                Table joinTable = (Table) rightItem;
                aliasMap.put(joinTable.getAlias().getName(), joinTable.getName());
            }
        }
        return aliasMap;
    }
}