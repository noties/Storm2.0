package storm.rx;

import storm.core.StormObject;
import storm.core.StormQuery;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormQueryRx<T extends StormObject> extends StormQuery<T> {

    StormQueryRx(StormQuery<T> parent) {
        super(parent.storm(), parent.table(), parent.query(), parent.dispatcher());
    }

    public StormQueryRxStream<T> stream() {
        return new StormQueryRxStream<>(this);
    }
}
