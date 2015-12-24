package storm.rx;

import storm.core.StormFill;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormFillRx<T extends StormObject> extends StormFill<T> implements StormRxOp {

    StormFillRx(StormFill<T> parent) {
        super(parent.storm(), parent.selection(), parent.value(), parent.dispatcher());
    }

    @Override
    public StormFillRxStream<T> stream() {
        return new StormFillRxStream<T>(this);
    }

    @Override
    public StormRx storm() {
        return (StormRx) super.storm();
    }

    @Override
    public StormFillRx<T> where() {
        return (StormFillRx<T>) super.where();
    }

    @Override
    public StormFillRx<T> equals(String col, Object arg) {
        return (StormFillRx<T>) super.equals(col, arg);
    }

    @Override
    public StormFillRx<T> greater(String col, Number arg) {
        return (StormFillRx<T>) super.greater(col, arg);
    }

    @Override
    public StormFillRx<T> groupStart() {
        return (StormFillRx<T>) super.groupStart();
    }

    @Override
    public StormFillRx<T> isNotNull(String col) {
        return (StormFillRx<T>) super.isNotNull(col);
    }

    @Override
    public StormFillRx<T> in(String col, Object... args) {
        return (StormFillRx<T>) super.in(col, args);
    }

    @Override
    public StormFillRx<T> less(String col, Number arg) {
        return (StormFillRx<T>) super.less(col, arg);
    }

    @Override
    public StormFillRx<T> raw(String statement, Object... args) {
        return (StormFillRx<T>) super.raw(statement, args);
    }

    @Override
    public StormFillRx<T> groupEnd() {
        return (StormFillRx<T>) super.groupEnd();
    }

    @Override
    public StormFillRx<T> notIn(String col, Object... args) {
        return (StormFillRx<T>) super.notIn(col, args);
    }

    @Override
    public StormFillRx<T> or() {
        return (StormFillRx<T>) super.or();
    }

    @Override
    public StormFillRx<T> lessEquals(String col, Number arg) {
        return (StormFillRx<T>) super.lessEquals(col, arg);
    }

    @Override
    public StormFillRx<T> isNull(String col) {
        return (StormFillRx<T>) super.isNull(col);
    }

    @Override
    public StormFillRx<T> glob(String col, String pattern) {
        return (StormFillRx<T>) super.glob(col, pattern);
    }

    @Override
    public StormFillRx<T> notEquals(String col, Object arg) {
        return (StormFillRx<T>) super.notEquals(col, arg);
    }

    @Override
    public StormFillRx<T> notGlob(String col, String pattern) {
        return (StormFillRx<T>) super.notGlob(col, pattern);
    }

    @Override
    public StormFillRx<T> and() {
        return (StormFillRx<T>) super.and();
    }

    @Override
    public StormFillRx<T> notLike(String col, String pattern) {
        return (StormFillRx<T>) super.notLike(col, pattern);
    }

    @Override
    public StormFillRx<T> greaterEquals(String col, Number arg) {
        return (StormFillRx<T>) super.greaterEquals(col, arg);
    }

    @Override
    public StormFillRx<T> like(String col, String pattern) {
        return (StormFillRx<T>) super.like(col, pattern);
    }

    @Override
    public StormFillRx<T> between(String col, Number from, Number to) {
        return (StormFillRx<T>) super.between(col, from, to);
    }

    @Override
    public StormFillRx<T> notBetween(String col, Number from, Number to) {
        return (StormFillRx<T>) super.notBetween(col, from, to);
    }
}
