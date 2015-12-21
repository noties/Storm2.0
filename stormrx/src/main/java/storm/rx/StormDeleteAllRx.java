package storm.rx;

import storm.core.StormDeleteAll;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormDeleteAllRx<T extends StormObject> extends StormDeleteAll<T> implements StormRxOp, StormDeleteRxType<T> {

    StormDeleteAllRx(StormDeleteAll<T> parent) {
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
}
