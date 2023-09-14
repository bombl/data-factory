package cn.thinkinginjava.data.core;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL解析器
 */
public class SQLParser {

    private final String sql;

    public SQLParser(String sql) {
        this.sql = sql;
    }

    public List<String> parse() {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            List<String> tableNames = tablesNamesFinder.getTableList(statement);
            return new ArrayList<>(tableNames);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
