package storm.rx;

import storm.core.StormObject;
import storm.core.StormSaveMany;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormSaveManyRx<T extends StormObject> extends StormSaveMany<T> implements StormRxOp {

    StormSaveManyRx(StormSaveMany<T> parent) {
        super(parent.storm(), parent.values(), parent.dispatcher());
    }

    @Override
    public StormSaveManyRxStream<T> stream() {
        return new StormSaveManyRxStream<>(this);
    }
}
