package storm.core;

import storm.query.OrderBy;
import storm.query.Query;
import storm.query.Selection;
import storm.query.Sorting;

/**
 * Created by Dimitry Ivanov on 20.12.2015.
 */
class StormQueryOp {
    
    private final Query mQuery;

    public StormQueryOp(Query query) {
        mQuery = query;
    }

    public Query query() {
        return mQuery;
    }

    public StormQueryOp from() {
        mQuery.from();
        return this;
    }

    public StormQueryOp orderBy(OrderBy first, OrderBy... others) {
        mQuery.orderBy(first, others);
        return this;
    }

    public StormQueryOp from(String table) {
        mQuery.from(table);
        return this;
    }

    public StormQueryOp where(Selection selection) {
        mQuery.where(selection);
        return this;
    }

    public StormQueryOp where(String condition, Object... args) {
        mQuery.where(condition, args);
        return this;
    }

    public StormQueryOp join(String table, String column, String... additionalColumns) {
        mQuery.join(table, column, additionalColumns);
        return this;
    }

    public StormQueryOp orderBy(String column, Sorting sorting) {
        mQuery.orderBy(column, sorting);
        return this;
    }

    public StormQueryOp groupEnd() {
        mQuery.groupEnd();
        return this;
    }

    public StormQueryOp raw(String statement, Object... args) {
        mQuery.raw(statement, args);
        return this;
    }

    public StormQueryOp groupStart() {
        mQuery.groupStart();
        return this;
    }

    public StormQueryOp groupBy(String groupBy, String having, Object... havingArgs) {
        mQuery.groupBy(groupBy, having, havingArgs);
        return this;
    }

    public StormQueryOp select(String... columns) {
        mQuery.select(columns);
        return this;
    }

    public StormQueryOp join(String table, Selection condition) {
        mQuery.join(table, condition);
        return this;
    }

    public StormQueryOp limit(long limit) {
        mQuery.limit(limit);
        return this;
    }

    public StormQueryOp offset(long offset) {
        mQuery.offset(offset);
        return this;
    }

    public StormQueryOp join() {
        mQuery.join();
        return this;
    }
}
