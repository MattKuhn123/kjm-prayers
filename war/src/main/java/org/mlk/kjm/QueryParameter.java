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

    public String getStringValue() {
        if (value instanceof LocalDate) {
            return dateToString((LocalDate) value);
        }
        
        return (String) value;
    }

    public String toSqlString() {
        return column + " " + getOperator() + " " + getSqlValue();
    }

    private String getSqlValue() {
        if (value instanceof LocalDate) {
            return " STR_TO_DATE(" + dateToString((LocalDate) value) + ", '%m/%d/%Y') ";
        }

        if (operator == QueryOperator.like) {
            return "%" + value + "%";
        }

        return (String) value;
    }
}
