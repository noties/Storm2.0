package storm.core;

import android.database.Cursor;

import java.util.List;

import storm.iterator.CursorIterator;
import storm.iterator.CursorIteratorCached;
import storm.query.OrderBy;
import storm.query.Query;
import storm.query.Selection;
import storm.query.Sorting;

/**
 * Created by Dimitry Ivanov on 16.12.2015.
 */
@SuppressWarnings("unchecked")
public class StormQuery<T extends StormObject> extends StormQueryOp {
    
    private final Storm mStorm;
    private final Class<T> mTable;
    private final StormQueryDispatcher mQueryDispatcher;
    
    protected StormQuery(Storm storm, Class<T> table, Query query, StormQueryDispatcher dispatcher) {
        super(query);
        this.mStorm = storm;
        this.mTable = table;
        this.mQueryDispatcher = dispatcher;
    }
    
    public Cursor asCursor(Storm storm, Query query) {
        return mQueryDispatcher.asCursor(storm, query);
    }

    public T asOne() {
        return mQueryDispatcher.asOne(mStorm, mTable, query());
    }

    public List<T> asList() {
        return mQueryDispatcher.asList(mStorm, mTable, query());
    }

    public CursorIteratorCached<T> asCachedIterator(int cacheSize) {
        return mQueryDispatcher.asCachedIterator(mStorm, mTable, query(), cacheSize);
    }

    public CursorIterator<T> asIterator() {
        return mQueryDispatcher.asIterator(mStorm, mTable, query());
    }

    public Storm storm() {
        return mStorm;
    }

    public Class<T> table() {
        return mTable;
    }

    public StormQueryDispatcher dispatcher() {
        return mQueryDispatcher;
    }


    @Override
    public StormQuery<T> from() {
        return (StormQuery<T>) super.from();
    }

    @Override
    public StormQuery<T> orderBy(OrderBy first, OrderBy... others) {
        return (StormQuery<T>) super.orderBy(first, others);
    }

    @Override
    public StormQuery<T> from(String table) {
        return (StormQuery<T>) super.from(table);
    }

    @Override
    public StormQuery<T> where(Selection selection) {
        return (StormQuery<T>) super.where(selection);
    }

    @Override
    public StormQuery<T> where(String condition, Object... args) {
        return (StormQuery<T>) super.where(condition, args);
    }

    @Override
    public StormQuery<T> join(String table, String column, String... additionalColumns) {
        return (StormQuery<T>) super.join(table, column, additionalColumns);
    }

    @Override
    public StormQuery<T> orderBy(String column, Sorting sorting) {
        return (StormQuery<T>) super.orderBy(column, sorting);
    }

    @Override
    public StormQuery<T> groupEnd() {
        return (StormQuery<T>) super.groupEnd();
    }

    @Override
    public StormQuery<T> raw(String statement, Object... args) {
        return (StormQuery<T>) super.raw(statement, args);
    }

    @Override
    public StormQuery<T> groupStart() {
        return (StormQuery<T>) super.groupStart();
    }

    @Override
    public StormQuery<T> groupBy(String groupBy, String having, Object... havingArgs) {
        return (StormQuery<T>) super.groupBy(groupBy, having, havingArgs);
    }

    @Override
    public StormQuery<T> select(String... columns) {
        return (StormQuery<T>) super.select(columns);
    }

    @Override
    public StormQuery<T> join(String table, Selection condition) {
        return (StormQuery<T>) super.join(table, condition);
    }

    @Override
    public StormQuery<T> limit(long limit) {
        return (StormQuery<T>) super.limit(limit);
    }

    @Override
    public StormQuery<T> join() {
        return (StormQuery<T>) super.join();
    }
}
