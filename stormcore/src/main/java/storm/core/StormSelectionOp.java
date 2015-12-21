package storm.core;

import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
class StormSelectionOp {
    
    private final Selection mSelection;

    StormSelectionOp(Selection selection) {
        mSelection = selection;
    }
    
    public Selection selection() {
        return mSelection;
    }

    public StormSelectionOp equals(String col, Object arg) {
        //noinspection ResultOfMethodCallIgnored
        mSelection.equals(col, arg);
        return this;
    }

    public StormSelectionOp greater(String col, Number arg) {
        mSelection.greater(col, arg);
        return this;
    }

    public StormSelectionOp groupStart() {
        mSelection.groupStart();
        return this;
    }

    public StormSelectionOp isNotNull(String col) {
        mSelection.isNotNull(col);
        return this;
    }

    public StormSelectionOp in(String col, Object... args) {
        mSelection.in(col, args);
        return this;
    }

    public StormSelectionOp less(String col, Number arg) {
        mSelection.less(col, arg);
        return this;
    }

    public StormSelectionOp raw(String statement, Object... args) {
        mSelection.raw(statement, args);
        return this;
    }

    public StormSelectionOp groupEnd() {
        mSelection.groupEnd();
        return this;
    }

    public StormSelectionOp notIn(String col, Object... args) {
        mSelection.notIn(col, args);
        return this;
    }

    public StormSelectionOp or() {
        mSelection.or();
        return this;
    }

    public StormSelectionOp lessEquals(String col, Number arg) {
        mSelection.lessEquals(col, arg);
        return this;
    }

    public StormSelectionOp isNull(String col) {
        mSelection.isNull(col);
        return this;
    }

    public StormSelectionOp glob(String col, String pattern) {
        mSelection.glob(col, pattern);
        return this;
    }

    public StormSelectionOp notEquals(String col, Object arg) {
        mSelection.notEquals(col, arg);
        return this;
    }

    public StormSelectionOp notGlob(String col, String pattern) {
        mSelection.notGlob(col, pattern);
        return this;
    }

    public StormSelectionOp and() {
        mSelection.and();
        return this;
    }

    public StormSelectionOp notLike(String col, String pattern) {
        mSelection.notLike(col, pattern);
        return this;
    }

    public StormSelectionOp greaterEquals(String col, Number arg) {
        mSelection.greaterEquals(col, arg);
        return this;
    }

    public StormSelectionOp like(String col, String pattern) {
        mSelection.like(col, pattern);
        return this;
    }

    public StormSelectionOp between(String col, Number from, Number to) {
        mSelection.between(col, from, to);
        return this;
    }

    public StormSelectionOp notBetween(String col, Number from, Number to) {
        mSelection.notBetween(col, from, to);
        return this;
    }
}
