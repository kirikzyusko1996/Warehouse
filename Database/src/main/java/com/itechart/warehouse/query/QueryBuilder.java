package com.itechart.warehouse.query;

import java.util.LinkedList;
import java.util.List;

/**
 * HQL query builder.
 */
class QueryBuilder {

    private boolean isFirstRestriction = true;

    private StringBuilder query;
    private List<String> joins = new LinkedList<>();
    private List<String> restrictions = new LinkedList<>();
    private String groupBy;
    private String orderBy;

    QueryBuilder() {
    }

    public void setRoot(StringBuilder query) {
        this.query = query;
    }


    QueryBuilder addJoin(String joinString) {
        if (joinString == null) {
            throw new IllegalArgumentException();
        }
        joins.add(joinString);
        return this;
    }

    QueryBuilder addRestriction(String restrictionString) {
        if (restrictionString == null) {
            throw new IllegalArgumentException();
        }
        restrictions.add(restrictionString);
        return this;
    }

    QueryBuilder addGroupBy(String projectionString) {
        if (projectionString == null) {
            throw new IllegalArgumentException();
        }
        this.groupBy = projectionString;
        return this;
    }

    QueryBuilder addOrderBy(String projectionString) {
        if (projectionString == null) {
            throw new IllegalArgumentException();
        }
        this.orderBy = projectionString;
        return this;
    }

    protected String build() {
        joins.forEach(this::appendWithSpace);
        for (String restriction : restrictions) {
            if (isFirstRestriction) {
                isFirstRestriction = false;
                where();
                appendWithSpace(restriction);
            } else {
                and();
                appendWithSpace(restriction);
            }
        }
        if (groupBy != null) {
            appendWithSpace(groupBy);
        }
        if (orderBy != null) {
            appendWithSpace(orderBy);
        }
        return query.toString();
    }


    private void appendWithSpace(String string) {
        query.append(" ").append(string);
    }

    private StringBuilder and() {
        return query.append(" AND ");
    }

    private StringBuilder where() {
        return query.append(" WHERE ");
    }


}
