package storm.rx;

import storm.core.StormObject;
import storm.core.StormUpdateOne;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormUpdateOneRx<T extends StormObject> extends StormUpdateOne<T> implements StormRxOp {

    StormUpdateOneRx(StormUpdateOne<T> parent) {
        super(parent.storm(), parent.value(), parent.dispatcher());
    }

    @Override
    public StormRx storm() {
        return (StormRx) super.storm();
    }

    @Override
    public StormUpdateOneRxStream<T> stream() {
        return new StormUpdateOneRxStream<T>(this);
    }
}
