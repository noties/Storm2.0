package storm.rx;

import storm.core.StormObject;
import storm.core.StormSimpleQuery;
import storm.query.OrderBy;
import storm.query.Selection;
import storm.query.Sorting;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormSimpleQueryRx<T extends StormObject> extends StormSimpleQuery<T> implements StormRxOp {

    public StormSimpleQueryRx(StormSimpleQuery<T> parent) {
        super(parent.storm(), parent.table(), parent.query(), parent.dispatcher());
    }

    @Override
    public StormSimpleQueryRxStream<T> stream() {
        return new StormSimpleQueryRxStream<T>(this);
    }

    @Override
    public StormRx storm() {
        return (StormRx) super.storm();
    }

    @Override
    public StormSimpleQueryRx<T> from() {
        return (StormSimpleQueryRx<T>) super.from();
    }

    @Override
    public StormSimpleQueryRx<T> orderBy(OrderBy first, OrderBy... others) {
        return (StormSimpleQueryRx<T>) super.orderBy(first, others);
    }

    @Override
    public StormSimpleQueryRx<T> from(String table) {
        return (StormSimpleQueryRx<T>) super.from(table);
    }

    @Override
    public StormSimpleQueryRx<T> where(Selection selection) {
        return (StormSimpleQueryRx<T>) super.where(selection);
    }

    @Override
    public StormSimpleQueryRx<T> where(String condition, Object... args) {
        return (StormSimpleQueryRx<T>) super.where(condition, args);
    }

    @Override
    public StormSimpleQueryRx<T> join(String table, String column, String... additionalColumns) {
        return (StormSimpleQueryRx<T>) super.join(table, column, additionalColumns);
    }

    @Override
    public StormSimpleQueryRx<T> orderBy(String column, Sorting sorting) {
        return (StormSimpleQueryRx<T>) super.orderBy(column, sorting);
    }

    @Override
    public StormSimpleQueryRx<T> groupEnd() {
        return (StormSimpleQueryRx<T>) super.groupEnd();
    }

    @Override
    public StormSimpleQueryRx<T> raw(String statement, Object... args) {
        return (StormSimpleQueryRx<T>) super.raw(statement, args);
    }

    @Override
    public StormSimpleQueryRx<T> groupStart() {
        return (StormSimpleQueryRx<T>) super.groupStart();
    }

    @Override
    public StormSimpleQueryRx<T> groupBy(String groupBy, String having, Object... havingArgs) {
        return (StormSimpleQueryRx<T>) super.groupBy(groupBy, having, havingArgs);
    }

    @Override
    public StormSimpleQueryRx<T> select(String... columns) {
        return (StormSimpleQueryRx<T>) super.select(columns);
    }

    @Override
    public StormSimpleQueryRx<T> join(String table, Selection condition) {
        return (StormSimpleQueryRx<T>) super.join(table, condition);
    }

    @Override
    public StormSimpleQueryRx<T> limit(long limit) {
        return (StormSimpleQueryRx<T>) super.limit(limit);
    }

    @Override
    public StormSimpleQueryRx<T> join() {
        return (StormSimpleQueryRx<T>) super.join();
    }
}
