package org.mlk.kjm;

import static org.mlk.kjm.ServletUtils.dateToString;

import java.time.LocalDate;

public class InsertValue {
    private final String column;
    private final Object value;
    public InsertValue(String column, Object value) {
        this.column = column;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }

    public String toSqlStringValue() {
        if (value instanceof LocalDate) {
            return " STR_TO_DATE(?, '%m/%d/%Y') ";
        }

        return "?";
    }

    public String toPreparedStatementValue() {
        if (value instanceof LocalDate) {
            return dateToString((LocalDate) value);
        }

        if (value == null) {
            return null;
        }

        return value.toString();
    }
}
