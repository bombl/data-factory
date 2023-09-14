package cn.thinkinginjava.data.core;

import cn.thinkinginjava.data.model.dto.DataGeneratorDTO;
import cn.thinkinginjava.data.model.dto.RelationshipField;
import cn.thinkinginjava.data.utils.RandomUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据生成器
 */
public class DataGenerator {
    // 指定生成的mock数据数量
    private final Integer generateNum;

    // 缓存表字段的关联字段生成的mock数据
    private final Map<String, List<Object>> cacheMap = new HashMap<>();

    // 指定表中字段取值范围
    private Map<String, List<Object>> defaultTableValue = new HashMap<>();

    // 表关联字段
    private final Map<String, RelationshipField> relationshipMap = new HashMap<>();

    // 表别名
    private Map<String, String> aliasMap = new HashMap<>();

    // 表字段条件
    private final Map<String, String> whereMap = new HashMap<>();

    // 表字段定义
    private final Map<String, List<ColumnDefinition>> tableColumnDefinitions = new HashMap<>();

    // 用到的表
    private final Set<String> involvedTables = new HashSet<>();

    // 查询SQL
    private final String querySql;

    // DDL列表
    private final List<String> ddlList;

    // DDL对应的表
    private final List<CreateTable> tableDefinitions = new ArrayList<>();

    // 表结果数据
    private final Map<String, List<String>> result = new HashMap<>();

    // 输入的表字段条件
    private final Map<String, String> extWhereMap;

    public DataGenerator(DataGeneratorDTO dataGeneratorDTO) {
        generateNum = dataGeneratorDTO.getGenerateNum();
        querySql = dataGeneratorDTO.getQuerySql();
        ddlList = dataGeneratorDTO.getDdlList();
        extWhereMap = dataGeneratorDTO.getExtWhereMap();
        if (dataGeneratorDTO.getDefaultTableValue() != null) {
            defaultTableValue = dataGeneratorDTO.getDefaultTableValue();
        }
    }

    public Map<String, List<String>> generateData() throws JSQLParserException {
        System.out.println("SQL: " + querySql);
        System.out.println("生成的数据：");
        PlainSelect plainSelect = getPlainSelect(querySql);

        rewriteSQL(plainSelect);

        saveTableDefinitions(ddlList);

        saveAlias(plainSelect);

        saveWhere(plainSelect);

        saveRelationship(plainSelect);

        applyExtWhereMap(extWhereMap);

        saveTableColumnDefinitions();

        saveInvolvedTables();

        generateMockData();
        return result;
    }

    private void applyExtWhereMap(Map<String, String> extWhereMap) {
        whereMap.putAll(extWhereMap);
    }

    public Map<String, String> parseWhereCondition() throws JSQLParserException {
        PlainSelect plainSelect = getPlainSelect(querySql);

        rewriteSQL(plainSelect);

        saveTableDefinitions(ddlList);

        saveAlias(plainSelect);

        saveWhere(plainSelect);

        saveRelationship(plainSelect);

        return whereMap;
    }

    private void rewriteSQL(PlainSelect plainSelect) throws JSQLParserException {
        new SqlRewriter(plainSelect).rewriteSQL();
    }

    private void saveTableDefinitions(List<String> ddlList) throws JSQLParserException {
        for (String ddl : ddlList) {
            tableDefinitions.add((CreateTable) CCJSqlParserUtil.parse(ddl));
        }
    }

    private PlainSelect getPlainSelect(String selectQuery) throws JSQLParserException {
        Select selectStatement = (Select) CCJSqlParserUtil.parse(selectQuery);
        return (PlainSelect) selectStatement.getSelectBody();
    }

    private void generateMockData() {
        for (String tableName : involvedTables) {
            List<ColumnDefinition> columnDefinitions = tableColumnDefinitions.get(tableName.toUpperCase(Locale.ROOT));
            generateDataForTable(tableName, columnDefinitions);
        }
    }

    private void saveInvolvedTables() throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(querySql);
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableNames = tablesNamesFinder.getTableList(statement);
        involvedTables.addAll(tableNames);
    }

    private void saveTableColumnDefinitions() {
        for (CreateTable tableDefinition : tableDefinitions) {
            String tableName = tableDefinition.getTable().getName();
            List<ColumnDefinition> columnDefinitions = tableDefinition.getColumnDefinitions();
            tableColumnDefinitions.put(tableName, columnDefinitions);
        }
    }

    private void saveWhere(PlainSelect plainSelect) {
        Expression expression = plainSelect.getWhere();
        if (expression instanceof AndExpression) {
            Expression leftExpression = ((AndExpression) expression).getLeftExpression();
            saveRelationship(leftExpression);
            Expression rightExpression = ((AndExpression) expression).getRightExpression();
            saveRelationship(rightExpression);
        }
    }

    private void generateDataForTable(String tableName, List<ColumnDefinition> columnDefinitions) {
        if (CollectionUtils.isEmpty(columnDefinitions)) {
            return;
        }
        for (int i = 0; i < generateNum; i++) {
            Map<String, Object> rowData = new LinkedHashMap<>();
            for (ColumnDefinition columnDefinition : columnDefinitions) {
                String columnName = columnDefinition.getColumnName().toUpperCase(Locale.ROOT);
                String tableFieldKey = tableName + "-" + columnName;
                RelationshipField relationshipField = relationshipMap.get(tableFieldKey);
                if (relationshipField != null) {
                    List<Object> values = cacheMap.get(relationshipField.getTableName() + "-" + relationshipField.getColumn().toUpperCase(Locale.ROOT));
                    if (values == null) {
                        List<Object> objects = cacheMap.get(tableFieldKey);
                        if (objects == null) {
                            objects = new ArrayList<>();
                        }
                        Object randomValue = RandomUtil.generateRandomValue(columnDefinition);
                        randomValue = getRandomValueByWhereCondition(tableFieldKey, columnDefinition, randomValue);
                        randomValue = getRandomValueByDefaultValue(tableFieldKey, randomValue);
                        objects.add(randomValue);
                        cacheMap.put(tableFieldKey, objects);
                        rowData.put(columnName, randomValue);
                    } else {
                        rowData.put(columnName, values.get(i));
                    }
                } else {
                    String dataType = columnDefinition.getColDataType().getDataType().toLowerCase();
                    if (dataType.contains("date")) {
                        String whereCondition = whereMap.get(tableFieldKey);
                        if (StringUtils.isNotBlank(whereCondition)) {
                            Object randomValue = RandomUtil.generateRandomDate(whereCondition);
                            rowData.put(columnName, randomValue);
                            continue;
                        }
                    }
                    Object randomValue = RandomUtil.generateRandomValue(columnDefinition);
                    randomValue = getRandomValueByWhereCondition(tableFieldKey, columnDefinition, randomValue);
                    randomValue = getRandomValueByDefaultValue(tableFieldKey, randomValue);
                    rowData.put(columnName, randomValue);
                }
            }
            generateInsertStatement(tableName, rowData);
        }
    }

    private Object getRandomValueByDefaultValue(String tableFieldKey, Object randomValue) {
        List<Object> objectList = defaultTableValue.get(tableFieldKey);
        if (!CollectionUtils.isEmpty(objectList)) {
            return RandomUtil.getRandomValueFromList(objectList);
        }
        return randomValue;
    }

    private Object getRandomValueByWhereCondition(String tableFieldKey, ColumnDefinition columnDefinition, Object randomValue) {
        String whereCondition = whereMap.get(tableFieldKey);
        if (whereCondition != null) {
            if ("IS NULL".equals(whereCondition)) {
                randomValue = "'NULL'";
            } else if (startWithCondition(whereCondition)) {
                randomValue = RandomUtil.generateRandomValue(columnDefinition, whereCondition);
            } else {
                randomValue = "'" + whereCondition + "'";
            }
        }
        return randomValue;
    }

    private boolean startWithCondition(String input) {
        if (input.toUpperCase(Locale.ROOT).startsWith("LIKE")
                || input.toUpperCase(Locale.ROOT).startsWith("IN")
                || input.toUpperCase(Locale.ROOT).startsWith("NOT IN")) {
            return Boolean.TRUE;
        }
        String regex = "^(>|>=|=|<=|<|!=)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String remainingPart = input.replaceAll("([><]=?|=|!=|\\s+)", "");
            if (input.startsWith("=")) {
                return Boolean.TRUE;
            } else {
                return remainingPart.matches("^[0-9]+(\\.[0-9]+)?$");
            }
        } else {
            return Boolean.FALSE;
        }
    }

    private void generateInsertStatement(String tableName, Map<String, Object> rowData) {
        StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder valuesSql = new StringBuilder("VALUES (");

        for (Map.Entry<String, Object> entry : rowData.entrySet()) {
            insertSql.append(entry.getKey());
            valuesSql.append(entry.getValue());
            insertSql.append(", ");
            valuesSql.append(", ");
        }
        insertSql.setLength(insertSql.length() - 2);
        valuesSql.setLength(valuesSql.length() - 2);
        insertSql.append(") ").append(valuesSql).append(");");
        List<String> dataList = result.get(tableName);
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        dataList.add(insertSql.toString());
        result.put(tableName, dataList);
        System.out.println(insertSql);
    }

    private void saveRelationship(PlainSelect plainSelect) {
        List<Join> joins = plainSelect.getJoins();
        if (CollectionUtils.isEmpty(joins)) {
            return;
        }
        for (Join join : joins) {
            Collection<Expression> onExpressions = join.getOnExpressions();
            for (Expression onExpression : onExpressions) {
                saveRelationship(onExpression);
            }
        }
    }

    private void saveRelationship(Expression expression) {
        if (expression instanceof EqualsTo) {
            Expression leftExpression = ((EqualsTo) expression).getLeftExpression();
            RelationshipField leftTable = getColumnName(leftExpression);
            Expression rightExpression = ((EqualsTo) expression).getRightExpression();
            RelationshipField rightTable = getColumnName(rightExpression);
            if (leftTable == null && rightTable == null) {
                return;
            }

            if (leftTable != null && leftTable.getTableName() != null
                    && rightTable != null && rightTable.getTableName() == null) {
                whereMap.put(leftTable.getTableName() + "-" + leftTable.getColumn().toUpperCase(Locale.ROOT), rightTable.getColumn());
                return;
            }

            if (rightTable != null && rightTable.getTableName() != null
                    && leftTable != null && leftTable.getTableName() == null) {
                whereMap.put(rightTable.getTableName() + "-" + rightTable.getColumn().toUpperCase(Locale.ROOT), leftTable.getColumn().toUpperCase(Locale.ROOT));
                return;
            }

            if (leftTable == null || rightTable == null) {
                return;
            }

            setRelationshipField(leftTable, rightTable);
            setRelationshipField(rightTable, leftTable);
        }

        if (expression instanceof InExpression) {
            InExpression inExpression = (InExpression) expression;
            Expression leftExpression = inExpression.getLeftExpression();
            RelationshipField leftTable = getColumnName(leftExpression);
            ItemsList rightItemsList = inExpression.getRightItemsList();
            String rightItems = rightItemsList.toString();
            if (inExpression.toString().toUpperCase(Locale.ROOT).contains("NOT")) {
                whereMap.put(leftTable.getTableName() + "-" + leftTable.getColumn().toUpperCase(Locale.ROOT), "NOT IN" + rightItems);
            } else {
                whereMap.put(leftTable.getTableName() + "-" + leftTable.getColumn().toUpperCase(Locale.ROOT), "IN" + rightItems);
            }
        }

        if (expression instanceof AndExpression) {
            Expression leftExpression = ((AndExpression) expression).getLeftExpression();
            saveRelationship(leftExpression);
            Expression rightExpression = ((AndExpression) expression).getRightExpression();
            saveRelationship(rightExpression);
        }

        if (expression instanceof GreaterThanEquals) {
            GreaterThanEquals greaterThanEquals = (GreaterThanEquals) expression;
            Expression leftExpression = greaterThanEquals.getLeftExpression();
            RelationshipField leftTable = getColumnName(leftExpression);
            Expression rightExpression = greaterThanEquals.getRightExpression();
            RelationshipField rightTable = getColumnName(rightExpression);
            whereMap.put(leftTable.getTableName() + "-" + leftTable.getColumn().toUpperCase(Locale.ROOT), ">=" + rightTable.getColumn());
        }

        if (expression instanceof GreaterThan) {
            GreaterThan greaterThan = (GreaterThan) expression;
            Expression leftExpression = greaterThan.getLeftExpression();
            RelationshipField leftTable = getColumnName(leftExpression);
            Expression rightExpression = greaterThan.getRightExpression();
            RelationshipField rightTable = getColumnName(rightExpression);
            whereMap.put(leftTable.getTableName() + "-" + leftTable.getColumn().toUpperCase(Locale.ROOT), ">" + rightTable.getColumn());
        }

        if (expression instanceof MinorThanEquals) {
            MinorThanEquals minorThanEquals = (MinorThanEquals) expression;
            Expression leftExpression = minorThanEquals.getLeftExpression();
            RelationshipField leftTable = getColumnName(leftExpression);
            Expression rightExpression = minorThanEquals.getRightExpression();
            RelationshipField rightTable = getColumnName(rightExpression);
            whereMap.put(leftTable.getTableName() + "-" + leftTable.getColumn().toUpperCase(Locale.ROOT), "<=" + rightTable.getColumn());
        }

        if (expression instanceof MinorThan) {
            MinorThan minorThan = (MinorThan) expression;
            Expression leftExpression = minorThan.getLeftExpression();
            RelationshipField leftTable = getColumnName(leftExpression);
            Expression rightExpression = minorThan.getRightExpression();
            RelationshipField rightTable = getColumnName(rightExpression);
            whereMap.put(leftTable.getTableName() + "-" + leftTable.getColumn().toUpperCase(Locale.ROOT), "<" + rightTable.getColumn());
        }

        if (expression instanceof LikeExpression) {
            LikeExpression likeExpression = (LikeExpression) expression;
            Expression leftExpression = likeExpression.getLeftExpression();
            RelationshipField leftTable = getColumnName(leftExpression);
            Expression rightExpression = likeExpression.getRightExpression();
            RelationshipField rightTable = getColumnName(rightExpression);
            whereMap.put(leftTable.getTableName() + "-" + leftTable.getColumn().toUpperCase(Locale.ROOT), "LIKE " + rightTable.getColumn());
        }

        if (expression instanceof NotEqualsTo) {
            NotEqualsTo notEqualsTo = (NotEqualsTo) expression;
            Expression leftExpression = notEqualsTo.getLeftExpression();
            RelationshipField leftTable = getColumnName(leftExpression);
            Expression rightExpression = notEqualsTo.getRightExpression();
            RelationshipField rightTable = getColumnName(rightExpression);
            whereMap.put(leftTable.getTableName() + "-" + leftTable.getColumn().toUpperCase(Locale.ROOT), "!=" + rightTable.getColumn());
        }

        if (expression instanceof IsNullExpression) {
            IsNullExpression isNullExpression = (IsNullExpression) expression;
            Expression leftExpression = isNullExpression.getLeftExpression();
            RelationshipField relationshipField = getColumnName(leftExpression);
            whereMap.put(relationshipField.getTableName() + "-" + relationshipField.getColumn().toUpperCase(Locale.ROOT), "IS NULL");
        }
    }

    private void setRelationshipField(RelationshipField leftTable, RelationshipField rightTable) {
        RelationshipField leftRelationshipField = relationshipMap.get(leftTable.getTableName() + "-" + leftTable.getColumn().toUpperCase(Locale.ROOT));
        if (leftRelationshipField == null) {
            RelationshipField relationshipField = new RelationshipField(rightTable.getTableName(), rightTable.getColumn().toUpperCase(Locale.ROOT));
            relationshipMap.put(leftTable.getTableName() + "-" + leftTable.getColumn().toUpperCase(Locale.ROOT), relationshipField);
        }
    }

    private RelationshipField getColumnName(Expression expression) {
        if (expression instanceof Column) {
            Column column = (Column) expression;
            RelationshipField relationshipField = new RelationshipField();
            relationshipField.setTableName(aliasMap.getOrDefault(column.getTable().getName(), column.getTable().getName()));
            relationshipField.setColumn(column.getColumnName());
            return relationshipField;
        }
        if (expression instanceof StringValue) {
            RelationshipField relationshipField = new RelationshipField();
            StringValue stringValue = (StringValue) expression;
            String value = stringValue.getValue();
            relationshipField.setColumn(value);
            return relationshipField;
        }
        if (expression instanceof LongValue) {
            RelationshipField relationshipField = new RelationshipField();
            LongValue stringValue = (LongValue) expression;
            String value = stringValue.getStringValue();
            relationshipField.setColumn(value);
            return relationshipField;
        }
        if (expression instanceof ExpressionList) {
            RelationshipField relationshipField = new RelationshipField();
            ExpressionList stringValue = (ExpressionList) expression;
            String value = stringValue.getExpressions().toString();
            relationshipField.setColumn(value);
            return relationshipField;
        }
        return null;
    }

    private void saveAlias(PlainSelect plainSelect) {
        this.aliasMap = new AliasPaser(plainSelect).parseAlias();
    }

}
