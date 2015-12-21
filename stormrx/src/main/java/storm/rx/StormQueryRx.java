package storm.rx;

import storm.core.StormObject;
import storm.core.StormQuery;
import storm.query.OrderBy;
import storm.query.Selection;
import storm.query.Sorting;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormQueryRx<T extends StormObject> extends StormQuery<T> implements StormRxOp {

    StormQueryRx(StormQuery<T> parent) {
        super(parent.storm(), parent.table(), parent.query(), parent.dispatcher());
    }

    @Override
    public StormRx storm() {
        return (StormRx) super.storm();
    }

    @Override
    public StormQueryRxStream<T> stream() {
        return new StormQueryRxStream<>(this);
    }

    @Override
    public StormQueryRx<T> from() {
        return (StormQueryRx<T>) super.from();
    }

    @Override
    public StormQueryRx<T> orderBy(OrderBy first, OrderBy... others) {
        return (StormQueryRx<T>) super.orderBy(first, others);
    }

    @Override
    public StormQueryRx<T> from(String table) {
        return (StormQueryRx<T>) super.from(table);
    }

    @Override
    public StormQueryRx<T> where(Selection selection) {
        return (StormQueryRx<T>) super.where(selection);
    }

    @Override
    public StormQueryRx<T> where(String condition, Object... args) {
        return (StormQueryRx<T>) super.where(condition, args);
    }

    @Override
    public StormQueryRx<T> join(String table, String column, String... additionalColumns) {
        return (StormQueryRx<T>) super.join(table, column, additionalColumns);
    }

    @Override
    public StormQueryRx<T> orderBy(String column, Sorting sorting) {
        return (StormQueryRx<T>) super.orderBy(column, sorting);
    }

    @Override
    public StormQueryRx<T> groupEnd() {
        return (StormQueryRx<T>) super.groupEnd();
    }

    @Override
    public StormQueryRx<T> raw(String statement, Object... args) {
        return (StormQueryRx<T>) super.raw(statement, args);
    }

    @Override
    public StormQueryRx<T> groupStart() {
        return (StormQueryRx<T>) super.groupStart();
    }

    @Override
    public StormQueryRx<T> groupBy(String groupBy, String having, Object... havingArgs) {
        return (StormQueryRx<T>) super.groupBy(groupBy, having, havingArgs);
    }

    @Override
    public StormQueryRx<T> select(String... columns) {
        return (StormQueryRx<T>) super.select(columns);
    }

    @Override
    public StormQueryRx<T> join(String table, Selection condition) {
        return (StormQueryRx<T>) super.join(table, condition);
    }

    @Override
    public StormQueryRx<T> limit(long limit) {
        return (StormQueryRx<T>) super.limit(limit);
    }

    @Override
    public StormQueryRx<T> join() {
        return (StormQueryRx<T>) super.join();
    }
}
