package storm.core;

import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
@SuppressWarnings("unchecked")
public class StormCount<T extends StormObject> extends StormSelectionOp implements StormOp<T> {

    private final Storm mStorm;
    private final Class<T> mTable;
    private final StormCountDispatcher mCountDispatcher;

    protected StormCount(Storm storm, Class<T> table, Selection selection, StormCountDispatcher countDispatcher) {
        super(selection);
        mStorm = storm;
        mTable = table;
        mCountDispatcher = countDispatcher;
    }

    public int execute() {
        return mCountDispatcher.execute(mStorm, mTable, selection());
    }


    @Override
    public StormCount<T> equals(String col, Object arg) {
        return (StormCount<T>) super.equals(col, arg);
    }

    @Override
    public StormCount<T> greater(String col, Number arg) {
        return (StormCount<T>) super.greater(col, arg);
    }

    @Override
    public StormCount<T> groupStart() {
        return (StormCount<T>) super.groupStart();
    }

    @Override
    public StormCount<T> isNotNull(String col) {
        return (StormCount<T>) super.isNotNull(col);
    }

    @Override
    public StormCount<T> in(String col, Object... args) {
        return (StormCount<T>) super.in(col, args);
    }

    @Override
    public StormCount<T> less(String col, Number arg) {
        return (StormCount<T>) super.less(col, arg);
    }

    @Override
    public StormCount<T> raw(String statement, Object... args) {
        return (StormCount<T>) super.raw(statement, args);
    }

    @Override
    public StormCount<T> groupEnd() {
        return (StormCount<T>) super.groupEnd();
    }

    @Override
    public StormCount<T> notIn(String col, Object... args) {
        return (StormCount<T>) super.notIn(col, args);
    }

    @Override
    public StormCount<T> or() {
        return (StormCount<T>) super.or();
    }

    @Override
    public StormCount<T> lessEquals(String col, Number arg) {
        return (StormCount<T>) super.lessEquals(col, arg);
    }

    @Override
    public StormCount<T> isNull(String col) {
        return (StormCount<T>) super.isNull(col);
    }

    @Override
    public StormCount<T> glob(String col, String pattern) {
        return (StormCount<T>) super.glob(col, pattern);
    }

    @Override
    public StormCount<T> notEquals(String col, Object arg) {
        return (StormCount<T>) super.notEquals(col, arg);
    }

    @Override
    public StormCount<T> notGlob(String col, String pattern) {
        return (StormCount<T>) super.notGlob(col, pattern);
    }

    @Override
    public StormCount<T> and() {
        return (StormCount<T>) super.and();
    }

    @Override
    public StormCount<T> notLike(String col, String pattern) {
        return (StormCount<T>) super.notLike(col, pattern);
    }

    @Override
    public StormCount<T> greaterEquals(String col, Number arg) {
        return (StormCount<T>) super.greaterEquals(col, arg);
    }

    @Override
    public StormCount<T> like(String col, String pattern) {
        return (StormCount<T>) super.like(col, pattern);
    }

    @Override
    public StormCount<T> between(String col, Number from, Number to) {
        return (StormCount<T>) super.between(col, from, to);
    }

    @Override
    public StormCount<T> notBetween(String col, Number from, Number to) {
        return (StormCount<T>) super.notBetween(col, from, to);
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
    public StormCountDispatcher dispatcher() {
        return mCountDispatcher;
    }
}
