package storm.rx;

import storm.core.StormDelete;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormDeleteRx<T extends StormObject> extends StormDelete<T> implements StormRxOp, StormDeleteRxType<T> {

    StormDeleteRx(StormDelete<T> parent) {
        super(parent.storm(), parent.table(), parent.selection(), parent.dispatcher());
    }

    @Override
    public StormDeleteRxStream<T> stream() {
        return new StormDeleteRxStream<T>(this);
    }

    @Override
    public StormRx storm() {
        return (StormRx) super.storm();
    }

    @Override
    public StormDeleteRx<T> equals(String col, Object arg) {
        return (StormDeleteRx<T>) super.equals(col, arg);
    }

    @Override
    public StormDeleteRx<T> greater(String col, Number arg) {
        return (StormDeleteRx<T>) super.greater(col, arg);
    }

    @Override
    public StormDeleteRx<T> groupStart() {
        return (StormDeleteRx<T>) super.groupStart();
    }

    @Override
    public StormDeleteRx<T> isNotNull(String col) {
        return (StormDeleteRx<T>) super.isNotNull(col);
    }

    @Override
    public StormDeleteRx<T> in(String col, Object... args) {
        return (StormDeleteRx<T>) super.in(col, args);
    }

    @Override
    public StormDeleteRx<T> less(String col, Number arg) {
        return (StormDeleteRx<T>) super.less(col, arg);
    }

    @Override
    public StormDeleteRx<T> raw(String statement, Object... args) {
        return (StormDeleteRx<T>) super.raw(statement, args);
    }

    @Override
    public StormDeleteRx<T> groupEnd() {
        return (StormDeleteRx<T>) super.groupEnd();
    }

    @Override
    public StormDeleteRx<T> notIn(String col, Object... args) {
        return (StormDeleteRx<T>) super.notIn(col, args);
    }

    @Override
    public StormDeleteRx<T> or() {
        return (StormDeleteRx<T>) super.or();
    }

    @Override
    public StormDeleteRx<T> lessEquals(String col, Number arg) {
        return (StormDeleteRx<T>) super.lessEquals(col, arg);
    }

    @Override
    public StormDeleteRx<T> isNull(String col) {
        return (StormDeleteRx<T>) super.isNull(col);
    }

    @Override
    public StormDeleteRx<T> glob(String col, String pattern) {
        return (StormDeleteRx<T>) super.glob(col, pattern);
    }

    @Override
    public StormDeleteRx<T> notEquals(String col, Object arg) {
        return (StormDeleteRx<T>) super.notEquals(col, arg);
    }

    @Override
    public StormDeleteRx<T> notGlob(String col, String pattern) {
        return (StormDeleteRx<T>) super.notGlob(col, pattern);
    }

    @Override
    public StormDeleteRx<T> and() {
        return (StormDeleteRx<T>) super.and();
    }

    @Override
    public StormDeleteRx<T> notLike(String col, String pattern) {
        return (StormDeleteRx<T>) super.notLike(col, pattern);
    }

    @Override
    public StormDeleteRx<T> greaterEquals(String col, Number arg) {
        return (StormDeleteRx<T>) super.greaterEquals(col, arg);
    }

    @Override
    public StormDeleteRx<T> like(String col, String pattern) {
        return (StormDeleteRx<T>) super.like(col, pattern);
    }

    @Override
    public StormDeleteRx<T> between(String col, Number from, Number to) {
        return (StormDeleteRx<T>) super.between(col, from, to);
    }

    @Override
    public StormDeleteRx<T> notBetween(String col, Number from, Number to) {
        return (StormDeleteRx<T>) super.notBetween(col, from, to);
    }
}
