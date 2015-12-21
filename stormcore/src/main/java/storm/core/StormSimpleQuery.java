package storm.core;

import storm.query.OrderBy;
import storm.query.Query;
import storm.query.Selection;
import storm.query.Sorting;

/**
 * Created by Dimitry Ivanov on 20.12.2015.
 */
public class StormSimpleQuery extends StormQueryOp implements StormOp {

    private final Storm mStorm;
    private final StormSimpleQueryDispatcher mSimpleQueryDispatcher;

    public StormSimpleQuery(Storm storm, Query query, StormSimpleQueryDispatcher simpleQueryDispatcher) {
        super(query);
        mStorm = storm;
        mSimpleQueryDispatcher = simpleQueryDispatcher;
    }

    @Override
    public Storm storm() {
        return mStorm;
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
    public StormSimpleQuery from() {
        return (StormSimpleQuery) super.from();
    }

    @Override
    public StormSimpleQuery orderBy(OrderBy first, OrderBy... others) {
        return (StormSimpleQuery) super.orderBy(first, others);
    }

    @Override
    public StormSimpleQuery from(String table) {
        return (StormSimpleQuery) super.from(table);
    }

    @Override
    public StormSimpleQuery where(Selection selection) {
        return (StormSimpleQuery) super.where(selection);
    }

    @Override
    public StormSimpleQuery where(String condition, Object... args) {
        return (StormSimpleQuery) super.where(condition, args);
    }

    @Override
    public StormSimpleQuery join(String table, String column, String... additionalColumns) {
        return (StormSimpleQuery) super.join(table, column, additionalColumns);
    }

    @Override
    public StormSimpleQuery orderBy(String column, Sorting sorting) {
        return (StormSimpleQuery) super.orderBy(column, sorting);
    }

    @Override
    public StormSimpleQuery groupEnd() {
        return (StormSimpleQuery) super.groupEnd();
    }

    @Override
    public StormSimpleQuery raw(String statement, Object... args) {
        return (StormSimpleQuery) super.raw(statement, args);
    }

    @Override
    public StormSimpleQuery groupStart() {
        return (StormSimpleQuery) super.groupStart();
    }

    @Override
    public StormSimpleQuery groupBy(String groupBy, String having, Object... havingArgs) {
        return (StormSimpleQuery) super.groupBy(groupBy, having, havingArgs);
    }

    @Override
    public StormSimpleQuery select(String... columns) {
        return (StormSimpleQuery) super.select(columns);
    }

    @Override
    public StormSimpleQuery join(String table, Selection condition) {
        return (StormSimpleQuery) super.join(table, condition);
    }

    @Override
    public StormSimpleQuery limit(long limit) {
        return (StormSimpleQuery) super.limit(limit);
    }

    @Override
    public StormSimpleQuery join() {
        return (StormSimpleQuery) super.join();
    }
}
