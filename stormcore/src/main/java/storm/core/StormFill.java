package storm.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
@SuppressWarnings("unchecked")
public class StormFill<T extends StormObject> extends StormSelectionOp implements StormOp<T> {
    
    private final Storm mStorm;
    private final T mValue;
    private final StormFillDispatcher mFillDispatcher;

    private List<String> mColumns;
    private boolean mIsInclude;


    protected StormFill(Storm storm, Selection selection, T value, StormFillDispatcher fillDispatcher) {
        super(selection);
        mStorm = storm;
        mValue = value;
        mFillDispatcher = fillDispatcher;
    }
    
    public int execute() {
        return mFillDispatcher.fill(mStorm, selection(), mColumns, mIsInclude, mValue);
    }

    public StormFill<T> includeColumns(String first, String... others) {
        mColumns = addColumns(first, others);
        mIsInclude = true;
        return this;
    }

    public StormFill<T> excludeColumns(String first, String... others) {
        mColumns = addColumns(first, others);
        mIsInclude = false;
        return this;
    }

    List<String> addColumns(String first, String... others) {
        final List<String> list = new ArrayList<>();
        list.add(first);
        if (others != null
                && others.length > 0) {
            Collections.addAll(list, others);
        }
        return list;
    }

    @Override
    public StormFill<T> where() {
        return (StormFill<T>) super.where();
    }

    @Override
    public StormFill<T> equals(String col, Object arg) {
        return (StormFill<T>) super.equals(col, arg);
    }

    @Override
    public StormFill<T> greater(String col, Number arg) {
        return (StormFill<T>) super.greater(col, arg);
    }

    @Override
    public StormFill<T> groupStart() {
        return (StormFill<T>) super.groupStart();
    }

    @Override
    public StormFill<T> isNotNull(String col) {
        return (StormFill<T>) super.isNotNull(col);
    }

    @Override
    public StormFill<T> in(String col, Object... args) {
        return (StormFill<T>) super.in(col, args);
    }

    @Override
    public StormFill<T> less(String col, Number arg) {
        return (StormFill<T>) super.less(col, arg);
    }

    @Override
    public StormFill<T> raw(String statement, Object... args) {
        return (StormFill<T>) super.raw(statement, args);
    }

    @Override
    public StormFill<T> groupEnd() {
        return (StormFill<T>) super.groupEnd();
    }

    @Override
    public StormFill<T> notIn(String col, Object... args) {
        return (StormFill<T>) super.notIn(col, args);
    }

    @Override
    public StormFill<T> or() {
        return (StormFill<T>) super.or();
    }

    @Override
    public StormFill<T> lessEquals(String col, Number arg) {
        return (StormFill<T>) super.lessEquals(col, arg);
    }

    @Override
    public StormFill<T> isNull(String col) {
        return (StormFill<T>) super.isNull(col);
    }

    @Override
    public StormFill<T> glob(String col, String pattern) {
        return (StormFill<T>) super.glob(col, pattern);
    }

    @Override
    public StormFill<T> notEquals(String col, Object arg) {
        return (StormFill<T>) super.notEquals(col, arg);
    }

    @Override
    public StormFill<T> notGlob(String col, String pattern) {
        return (StormFill<T>) super.notGlob(col, pattern);
    }

    @Override
    public StormFill<T> and() {
        return (StormFill<T>) super.and();
    }

    @Override
    public StormFill<T> notLike(String col, String pattern) {
        return (StormFill<T>) super.notLike(col, pattern);
    }

    @Override
    public StormFill<T> greaterEquals(String col, Number arg) {
        return (StormFill<T>) super.greaterEquals(col, arg);
    }

    @Override
    public StormFill<T> like(String col, String pattern) {
        return (StormFill<T>) super.like(col, pattern);
    }

    @Override
    public StormFill<T> between(String col, Number from, Number to) {
        return (StormFill<T>) super.between(col, from, to);
    }

    @Override
    public StormFill<T> notBetween(String col, Number from, Number to) {
        return (StormFill<T>) super.notBetween(col, from, to);
    }

    @Override
    public Storm storm() {
        return mStorm;
    }

    @Override
    public Class<T> table() {
        return (Class<T>) mValue.getClass();
    }

    @Override
    public StormFillDispatcher dispatcher() {
        return mFillDispatcher;
    }

    public T value() {
        return mValue;
    }

    public List<String> columns() {
        return mColumns;
    }

    public boolean isInclude() {
        return mIsInclude;
    }
}
