package storm.rx;

import storm.core.StormSimpleQuery;
import storm.query.OrderBy;
import storm.query.Selection;
import storm.query.Sorting;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormSimpleQueryRx extends StormSimpleQuery implements StormRxOp {

    public StormSimpleQueryRx(StormSimpleQuery parent) {
        super(parent.storm(), parent.query(), parent.dispatcher());
    }

    @Override
    public StormSimpleQueryRxStream stream() {
        return new StormSimpleQueryRxStream(this);
    }

    @Override
    public StormSimpleQueryRx from() {
        return (StormSimpleQueryRx) super.from();
    }

    @Override
    public StormSimpleQueryRx orderBy(OrderBy first, OrderBy... others) {
        return (StormSimpleQueryRx) super.orderBy(first, others);
    }

    @Override
    public StormSimpleQueryRx from(String table) {
        return (StormSimpleQueryRx) super.from(table);
    }

    @Override
    public StormSimpleQueryRx where(Selection selection) {
        return (StormSimpleQueryRx) super.where(selection);
    }

    @Override
    public StormSimpleQueryRx where(String condition, Object... args) {
        return (StormSimpleQueryRx) super.where(condition, args);
    }

    @Override
    public StormSimpleQueryRx join(String table, String column, String... additionalColumns) {
        return (StormSimpleQueryRx) super.join(table, column, additionalColumns);
    }

    @Override
    public StormSimpleQueryRx orderBy(String column, Sorting sorting) {
        return (StormSimpleQueryRx) super.orderBy(column, sorting);
    }

    @Override
    public StormSimpleQueryRx groupEnd() {
        return (StormSimpleQueryRx) super.groupEnd();
    }

    @Override
    public StormSimpleQueryRx raw(String statement, Object... args) {
        return (StormSimpleQueryRx) super.raw(statement, args);
    }

    @Override
    public StormSimpleQueryRx groupStart() {
        return (StormSimpleQueryRx) super.groupStart();
    }

    @Override
    public StormSimpleQueryRx groupBy(String groupBy, String having, Object... havingArgs) {
        return (StormSimpleQueryRx) super.groupBy(groupBy, having, havingArgs);
    }

    @Override
    public StormSimpleQueryRx select(String... columns) {
        return (StormSimpleQueryRx) super.select(columns);
    }

    @Override
    public StormSimpleQueryRx join(String table, Selection condition) {
        return (StormSimpleQueryRx) super.join(table, condition);
    }

    @Override
    public StormSimpleQueryRx limit(long limit) {
        return (StormSimpleQueryRx) super.limit(limit);
    }

    @Override
    public StormSimpleQueryRx join() {
        return (StormSimpleQueryRx) super.join();
    }
}
