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
public class StormQuery<T extends StormObject> {
    
    private final Storm mStorm;
    private final Class<T> mTable;
    private final Query mQuery;
    private final StormQueryDispatcher mQueryDispatcher;
    
    StormQuery(Storm storm, Class<T> table, Query query, StormQueryDispatcher dispatcher) {
        this.mStorm = storm;
        this.mTable = table;
        this.mQuery = query;
        this.mQueryDispatcher = dispatcher;
    }

    public StormQuery<T> select(String... columns) {
        mQuery.select(columns);
        return this;
    }

    public StormQuery<T> where(Selection selection) {
        mQuery.where(selection);
        return this;
    }

    public StormQuery<T> orderBy(String column, Sorting sorting) {
        mQuery.orderBy(column, sorting);
        return this;
    }

    public StormQuery<T> from(String table) {
        mQuery.from(table);
        return this;
    }

    public StormQuery<T> groupBy(String groupBy, String having, Object... havingArgs) {
        mQuery.groupBy(groupBy, having, havingArgs);
        return this;
    }

    public StormQuery<T> join(String table, String column, String... additionalColumns) {
        mQuery.join(table, column, additionalColumns);
        return this;
    }

    public StormQuery<T> limit(long limit) {
        mQuery.limit(limit);
        return this;
    }

    public StormQuery<T> groupEnd() {
        mQuery.groupEnd();
        return this;
    }

    public StormQuery<T> raw(String statement, Object... args) {
        mQuery.raw(statement, args);
        return this;
    }

    public StormQuery<T> from() {
        mQuery.from();
        return this;
    }

    public StormQuery<T> where(String condition, Object... args) {
        mQuery.where(condition, args);
        return this;
    }

    public StormQuery<T> join() {
        mQuery.join();
        return this;
    }

    public StormQuery<T> join(String table, Selection condition) {
        mQuery.join(table, condition);
        return this;
    }

    public StormQuery<T> orderBy(OrderBy first, OrderBy... others) {
        mQuery.orderBy(first, others);
        return this;
    }

    public StormQuery<T> groupStart() {
        mQuery.groupStart();
        return this;
    }

    Query getQuery() {
        return mQuery;
    }

    public Cursor asCursor(Storm storm, Query query) {
        return mQueryDispatcher.asCursor(storm, query);
    }

    public T asOne() {
        return mQueryDispatcher.asOne(mStorm, mTable, mQuery);
    }

    public List<T> asList() {
        return mQueryDispatcher.asList(mStorm, mTable, mQuery);
    }

    public CursorIteratorCached<T> asCachedIterator(int cacheSize) {
        return mQueryDispatcher.asCachedIterator(mStorm, mTable, mQuery, cacheSize);
    }

    public CursorIterator<T> asIterator() {
        return mQueryDispatcher.asIterator(mStorm, mTable, mQuery);
    }
}
