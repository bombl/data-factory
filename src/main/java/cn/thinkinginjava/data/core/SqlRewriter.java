package cn.thinkinginjava.data.core;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * SQL重写器
 */
public class SqlRewriter {

    private final PlainSelect plainSelect;

    public SqlRewriter(PlainSelect plainSelect) {
        this.plainSelect = plainSelect;
    }

    public void rewriteSQL() throws JSQLParserException {
        Expression whereCondition = plainSelect.getWhere();
        if (whereCondition == null) {
            return;
        }
        rewriteExists(plainSelect, whereCondition);
        rewriteSubSelect(plainSelect, whereCondition);
    }

    private void rewriteSubSelect(PlainSelect plainSelect, Expression whereCondition) {
        List<Join> joins = plainSelect.getJoins();
        if (CollectionUtils.isEmpty(joins)) {
            return;
        }
        for (Join join : joins) {
            FromItem rightItem = join.getRightItem();
            if (!(rightItem instanceof SubSelect)) {
                continue;
            }
            SubSelect subSelect = (SubSelect) rightItem;
            PlainSelect subqueryPlainSelect = (PlainSelect) subSelect.getSelectBody();
            Expression subWhereCondition = subqueryPlainSelect.getWhere();
            if (subWhereCondition != null) {
                subWhereCondition.accept(new ExpressionDeParser() {
                    @Override
                    public void visit(Column tableColumn) {
                        super.visit(tableColumn);
                        tableColumn.setTable((Table) subqueryPlainSelect.getFromItem());
                    }
                });
                AndExpression mergedCondition = new AndExpression(whereCondition, subWhereCondition);
                plainSelect.setWhere(mergedCondition);
            }

            Join subJoin = new Join();
            FromItem fromItem = subqueryPlainSelect.getFromItem();
            fromItem.setAlias(subSelect.getAlias());
            subJoin.setRightItem(fromItem);
            Collection<Expression> onExpressions = join.getOnExpressions();
            for (Expression expression : onExpressions) {
                subJoin.addOnExpression(expression);
            }
            plainSelect.addJoins(subJoin);
            joins.remove(join);
        }
    }

    private void rewriteExists(PlainSelect plainSelect, Expression whereCondition) throws JSQLParserException {
        if (whereCondition.toString().toUpperCase().contains("EXISTS")) {
            // 解析 EXISTS 子查询条件
            String subquery = whereCondition.toString();
            int startIndex = subquery.toUpperCase().indexOf("EXISTS");
            int endIndex = subquery.indexOf(")", startIndex);
            String existsCondition = subquery.substring(startIndex, endIndex + 1);
            String subquerySQL = existsCondition.replaceAll("(?i)EXISTS", "").trim();
            if (subquerySQL.startsWith("(") && subquerySQL.endsWith(")")) {
                subquerySQL = subquerySQL.substring(1, subquerySQL.length() - 1);
            }
            PlainSelect subqueryPlainSelect = getPlainSelect(subquerySQL);
            Join join = new Join();
            join.setRightItem(subqueryPlainSelect.getFromItem());
            Expression onExpression = CCJSqlParserUtil.parseExpression(subqueryPlainSelect.getWhere().toString());

            join.addOnExpression(onExpression);
            plainSelect.addJoins(join);
        }
    }

    private PlainSelect getPlainSelect(String selectQuery) throws JSQLParserException {
        Select selectStatement = (Select) CCJSqlParserUtil.parse(selectQuery);
        return (PlainSelect) selectStatement.getSelectBody();
    }
}
