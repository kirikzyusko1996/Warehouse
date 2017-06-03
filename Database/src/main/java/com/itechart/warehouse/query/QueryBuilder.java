package com.itechart.warehouse.query;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * HQL query builder.
 */
public class QueryBuilder {
    Logger logger = LoggerFactory.getLogger(QueryBuilder.class);

    private SessionFactory sessionFactory;
    private boolean isFirstRestriction = true;

    private StringBuilder query;
    private List<String> joins = new LinkedList<>();
    private List<String> restrictions = new LinkedList<>();
    private String groupBy;
    private String orderBy;


    public QueryBuilder(StringBuilder query) {
        this.query = query;
    }


    public QueryBuilder addJoin(String joinString) {
        if (joinString == null) throw new IllegalArgumentException();
        joins.add(joinString);
        return this;
    }

    public QueryBuilder addRestriction(String restrictionString) {
        if (restrictionString == null) throw new IllegalArgumentException();
        restrictions.add(restrictionString);
        return this;
    }

    public QueryBuilder addGroupBy(String projectionString) {
        if (projectionString == null) throw new IllegalArgumentException();
        this.groupBy = projectionString;
        return this;
    }
    public QueryBuilder addOrderBy(String projectionString) {
        if (projectionString == null) throw new IllegalArgumentException();
        this.orderBy = projectionString;
        return this;
    }

    public String build() {
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

//    private enum JoinType {
//        INNER_JOIN("INNER JOIN"), LEFT_JOIN("LEFT OUTER JOIN"), RIGHT_JOIN("RIGHT OUTER JOIN");
//        private String string;
//
//        JoinType(String string) {
//            this.string = string;
//        }
//
//        @Override
//        public String toString() {
//            return string;
//        }
//    }

    private void appendWithSpace(String string) {
        query.append(" ").append(string);
    }

    private StringBuilder and() {
        return query.append(" AND ");
    }

    private StringBuilder where() {
        return query.append(" WHERE ");
    }

//    private StringBuilder join(JoinType type) {
//        return query.append(type.string);
//    }


}
