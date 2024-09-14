package org.mlk.kjm;

public class OrderByClause {
    private final String orderBy;
    private final boolean isAsc;

    public OrderByClause(String orderBy, boolean isAsc) {
        this.orderBy = orderBy;
        this.isAsc = isAsc;
    }
    
    public String getOrderBy() {
        return orderBy;
    }

    public boolean getIsAsc() {
        return isAsc;
    }

    public String toSqlStringValue() {
        return orderBy + (isAsc ? " ASC " : " DESC ");
    }
}
