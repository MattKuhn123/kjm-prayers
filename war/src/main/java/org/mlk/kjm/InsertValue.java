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
        String qMark = (value instanceof LocalDate) 
            ? " STR_TO_DATE(?, '%m/%d/%Y') " 
            : "?";
        return qMark;
    }

    public String toPreparedStatementValue() {
        String result = (value instanceof LocalDate)
            ? dateToString((LocalDate) value)
            : (String) value;

        return result;
    }
}
