package storm.rx;

import storm.core.StormCount;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormCountRx<T extends StormObject> extends StormCount<T> implements StormRxOp {

    StormCountRx(StormCount<T> parent) {
        super(parent.storm(), parent.table(), parent.selection(), parent.dispatcher());
    }

    @Override
    public StormCountRxStream<T> stream() {
        return new StormCountRxStream<>(this);
    }

    @Override
    public StormRx storm() {
        return (StormRx) super.storm();
    }

    @Override
    public StormCountRx<T> equals(String col, Object arg) {
        return (StormCountRx<T>) super.equals(col, arg);
    }

    @Override
    public StormCountRx<T> greater(String col, Number arg) {
        return (StormCountRx<T>) super.greater(col, arg);
    }

    @Override
    public StormCountRx<T> groupStart() {
        return (StormCountRx<T>) super.groupStart();
    }

    @Override
    public StormCountRx<T> isNotNull(String col) {
        return (StormCountRx<T>) super.isNotNull(col);
    }

    @Override
    public StormCountRx<T> in(String col, Object... args) {
        return (StormCountRx<T>) super.in(col, args);
    }

    @Override
    public StormCountRx<T> less(String col, Number arg) {
        return (StormCountRx<T>) super.less(col, arg);
    }

    @Override
    public StormCountRx<T> raw(String statement, Object... args) {
        return (StormCountRx<T>) super.raw(statement, args);
    }

    @Override
    public StormCountRx<T> groupEnd() {
        return (StormCountRx<T>) super.groupEnd();
    }

    @Override
    public StormCountRx<T> notIn(String col, Object... args) {
        return (StormCountRx<T>) super.notIn(col, args);
    }

    @Override
    public StormCountRx<T> or() {
        return (StormCountRx<T>) super.or();
    }

    @Override
    public StormCountRx<T> lessEquals(String col, Number arg) {
        return (StormCountRx<T>) super.lessEquals(col, arg);
    }

    @Override
    public StormCountRx<T> isNull(String col) {
        return (StormCountRx<T>) super.isNull(col);
    }

    @Override
    public StormCountRx<T> glob(String col, String pattern) {
        return (StormCountRx<T>) super.glob(col, pattern);
    }

    @Override
    public StormCountRx<T> notEquals(String col, Object arg) {
        return (StormCountRx<T>) super.notEquals(col, arg);
    }

    @Override
    public StormCountRx<T> notGlob(String col, String pattern) {
        return (StormCountRx<T>) super.notGlob(col, pattern);
    }

    @Override
    public StormCountRx<T> and() {
        return (StormCountRx<T>) super.and();
    }

    @Override
    public StormCountRx<T> notLike(String col, String pattern) {
        return (StormCountRx<T>) super.notLike(col, pattern);
    }

    @Override
    public StormCountRx<T> greaterEquals(String col, Number arg) {
        return (StormCountRx<T>) super.greaterEquals(col, arg);
    }

    @Override
    public StormCountRx<T> like(String col, String pattern) {
        return (StormCountRx<T>) super.like(col, pattern);
    }

    @Override
    public StormCountRx<T> between(String col, Number from, Number to) {
        return (StormCountRx<T>) super.between(col, from, to);
    }

    @Override
    public StormCountRx<T> notBetween(String col, Number from, Number to) {
        return (StormCountRx<T>) super.notBetween(col, from, to);
    }
}
