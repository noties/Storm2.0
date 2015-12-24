package storm.core;

import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
@SuppressWarnings("unchecked")
public class StormDelete<T extends StormObject> extends StormSelectionOp implements StormOp<T> {
    
    private final Storm mStorm;
    private final Class<T> mTable;
    private final StormDeleteDispatcher mDeleteDispatcher;
    
    protected StormDelete(Storm storm, Class<T> table, Selection selection, StormDeleteDispatcher deleteDispatcher) {
        super(selection);
        mStorm = storm;
        mTable = table;
        mDeleteDispatcher = deleteDispatcher;
    }

    public int execute() {
        return mDeleteDispatcher.execute(mStorm, mTable, selection());
    }

    @Override
    public StormDelete<T> where() {
        return (StormDelete<T>) super.where();
    }

    @Override
    public StormDelete<T> equals(String col, Object arg) {
        return (StormDelete<T>) super.equals(col, arg);
    }

    @Override
    public StormDelete<T> greater(String col, Number arg) {
        return (StormDelete<T>) super.greater(col, arg);
    }

    @Override
    public StormDelete<T> groupStart() {
        return (StormDelete<T>) super.groupStart();
    }

    @Override
    public StormDelete<T> isNotNull(String col) {
        return (StormDelete<T>) super.isNotNull(col);
    }

    @Override
    public StormDelete<T> in(String col, Object... args) {
        return (StormDelete<T>) super.in(col, args);
    }

    @Override
    public StormDelete<T> less(String col, Number arg) {
        return (StormDelete<T>) super.less(col, arg);
    }

    @Override
    public StormDelete<T> raw(String statement, Object... args) {
        return (StormDelete<T>) super.raw(statement, args);
    }

    @Override
    public StormDelete<T> groupEnd() {
        return (StormDelete<T>) super.groupEnd();
    }

    @Override
    public StormDelete<T> notIn(String col, Object... args) {
        return (StormDelete<T>) super.notIn(col, args);
    }

    @Override
    public StormDelete<T> or() {
        return (StormDelete<T>) super.or();
    }

    @Override
    public StormDelete<T> lessEquals(String col, Number arg) {
        return (StormDelete<T>) super.lessEquals(col, arg);
    }

    @Override
    public StormDelete<T> isNull(String col) {
        return (StormDelete<T>) super.isNull(col);
    }

    @Override
    public StormDelete<T> glob(String col, String pattern) {
        return (StormDelete<T>) super.glob(col, pattern);
    }

    @Override
    public StormDelete<T> notEquals(String col, Object arg) {
        return (StormDelete<T>) super.notEquals(col, arg);
    }

    @Override
    public StormDelete<T> notGlob(String col, String pattern) {
        return (StormDelete<T>) super.notGlob(col, pattern);
    }

    @Override
    public StormDelete<T> and() {
        return (StormDelete<T>) super.and();
    }

    @Override
    public StormDelete<T> notLike(String col, String pattern) {
        return (StormDelete<T>) super.notLike(col, pattern);
    }

    @Override
    public StormDelete<T> greaterEquals(String col, Number arg) {
        return (StormDelete<T>) super.greaterEquals(col, arg);
    }

    @Override
    public StormDelete<T> like(String col, String pattern) {
        return (StormDelete<T>) super.like(col, pattern);
    }

    @Override
    public StormDelete<T> between(String col, Number from, Number to) {
        return (StormDelete<T>) super.between(col, from, to);
    }

    @Override
    public StormDelete<T> notBetween(String col, Number from, Number to) {
        return (StormDelete<T>) super.notBetween(col, from, to);
    }

    @Override
    public Storm storm() {
        return mStorm;
    }

    @Override
    public Class<T> table() {
        return mTable;
    }

    @Override
    public StormDeleteDispatcher dispatcher() {
        return mDeleteDispatcher;
    }
}
