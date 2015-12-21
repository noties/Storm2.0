package storm.rx;

import storm.core.StormObject;
import storm.core.StormUpdateMany;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormUpdateManyRx<T extends StormObject> extends StormUpdateMany<T> implements StormRxOp {

    StormUpdateManyRx(StormUpdateMany<T> parent) {
        super(parent.storm(), parent.values(), parent.dispatcher());
    }

    @Override
    public StormUpdateManyRxStream<T> stream() {
        return new StormUpdateManyRxStream<T>(this);
    }

    @Override
    public StormRx storm() {
        return (StormRx) super.storm();
    }
}
