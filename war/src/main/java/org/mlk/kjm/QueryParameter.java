package org.mlk.kjm;

import static org.mlk.kjm.ServletUtils.dateToString;

import java.time.LocalDate;

public class QueryParameter {
    public enum QueryOperator {
        equals,
        like,
        greaterThan,
        greaterThanOrEqualTo,
        lessThan,
        lessThanOrEqualTo,
    };

    private final String column;
    private final QueryOperator operator;
    private final Object value;

    public QueryParameter(String column, QueryOperator operator, Object value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public String getOperator() {
        switch (operator) {
            case equals: return "=";
            case like: return " LIKE ";
            case greaterThan: return ">";
            case greaterThanOrEqualTo: return ">=";
            case lessThan: return "<";
            case lessThanOrEqualTo: return "<=";
            default: return "=";
        }
    }

    public String toPreparedStatementValue() {
        String wildcard = operator == QueryOperator.like 
            ? "%" 
            : "";
        
        if (value instanceof LocalDate) {
            String str = dateToString((LocalDate) value);
            return wildcard + str + wildcard;
        }
        
        if (value instanceof Boolean) {
            String str = ((Boolean) value) ? "1" : "0";
            return wildcard + str + wildcard;
        }
        
        return wildcard + value.toString() + wildcard;
    }

    public String toSqlStringValue() {
        String qMark = (value instanceof LocalDate) 
            ? " STR_TO_DATE(?, '%m/%d/%Y') " 
            : "?";
        return column + " " + getOperator() + " " + qMark;
    }
}
