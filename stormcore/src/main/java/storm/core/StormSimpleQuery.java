package storm.core;

import storm.query.OrderBy;
import storm.query.Query;
import storm.query.Selection;
import storm.query.Sorting;

/**
 * Created by Dimitry Ivanov on 20.12.2015.
 */
@SuppressWarnings("unchecked")
public class StormSimpleQuery<T extends StormObject> extends StormQueryOp implements StormOp<T> {

    private final Storm mStorm;
    private final Class<T> mTable;
    private final StormSimpleQueryDispatcher mSimpleQueryDispatcher;

    public StormSimpleQuery(Storm storm, Class<T> table, Query query, StormSimpleQueryDispatcher simpleQueryDispatcher) {
        super(query);
        mStorm = storm;
        mTable = table;
        mSimpleQueryDispatcher = simpleQueryDispatcher;
    }

    @Override
    public Storm storm() {
        return mStorm;
    }

    @Override
    public Class<T> table() {
        return mTable;
    }

    public StormSimpleQueryDispatcher dispatcher() {
        return mSimpleQueryDispatcher;
    }

    public int asInt() {
        return asInt(0);
    }

    public int asInt(int defValue) {
        return mSimpleQueryDispatcher.asInt(mStorm, query(), defValue);
    }

    public long asLong() {
        return asLong(0L);
    }

    public long asLong(long defValue) {
        return mSimpleQueryDispatcher.asLong(mStorm, query(), defValue);
    }

    public float asFloat() {
        return asFloat(.0F);
    }

    public float asFloat(float defValue) {
        return mSimpleQueryDispatcher.asFloat(mStorm, query(), defValue);
    }

    public double asDouble() {
        return asDouble(.0D);
    }

    public double asDouble(double defValue) {
        return mSimpleQueryDispatcher.asDouble(mStorm, query(), defValue);
    }

    public String asString() {
        return asString(null);
    }

    public String asString(String defValue) {
        return mSimpleQueryDispatcher.asString(mStorm, query(), defValue);
    }

    public byte[] asByteArray() {
        return asByteArray(null);
    }

    public byte[] asByteArray(byte[] defValue) {
        return mSimpleQueryDispatcher.asByteArray(mStorm, query(), defValue);
    }

    @Override
    public StormSimpleQuery<T> from() {
        return (StormSimpleQuery<T>) super.from();
    }

    @Override
    public StormSimpleQuery<T> orderBy(OrderBy first, OrderBy... others) {
        return (StormSimpleQuery<T>) super.orderBy(first, others);
    }

    @Override
    public StormSimpleQuery<T> from(String table) {
        return (StormSimpleQuery<T>) super.from(table);
    }

    @Override
    public StormSimpleQuery<T> where(Selection selection) {
        return (StormSimpleQuery<T>) super.where(selection);
    }

    @Override
    public StormSimpleQuery<T> where(String condition, Object... args) {
        return (StormSimpleQuery<T>) super.where(condition, args);
    }

    @Override
    public StormSimpleQuery<T> join(String table, String column, String... additionalColumns) {
        return (StormSimpleQuery<T>) super.join(table, column, additionalColumns);
    }

    @Override
    public StormSimpleQuery<T> orderBy(String column, Sorting sorting) {
        return (StormSimpleQuery<T>) super.orderBy(column, sorting);
    }

    @Override
    public StormSimpleQuery<T> groupEnd() {
        return (StormSimpleQuery<T>) super.groupEnd();
    }

    @Override
    public StormSimpleQuery<T> raw(String statement, Object... args) {
        return (StormSimpleQuery<T>) super.raw(statement, args);
    }

    @Override
    public StormSimpleQuery<T> groupStart() {
        return (StormSimpleQuery<T>) super.groupStart();
    }

    @Override
    public StormSimpleQuery<T> groupBy(String groupBy, String having, Object... havingArgs) {
        return (StormSimpleQuery<T>) super.groupBy(groupBy, having, havingArgs);
    }

    @Override
    public StormSimpleQuery<T> select(String... columns) {
        return (StormSimpleQuery<T>) super.select(columns);
    }

    @Override
    public StormSimpleQuery<T> join(String table, Selection condition) {
        return (StormSimpleQuery<T>) super.join(table, condition);
    }

    @Override
    public StormSimpleQuery<T> limit(long limit) {
        return (StormSimpleQuery<T>) super.limit(limit);
    }

    @Override
    public StormSimpleQuery<T> join() {
        return (StormSimpleQuery<T>) super.join();
    }
}
