package storm.rx;

import storm.core.StormObject;
import storm.core.StormSaveOne;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormSaveOneRx<T extends StormObject> extends StormSaveOne<T> implements StormRxOp {

    StormSaveOneRx(StormSaveOne<T> saveOne) {
        super(saveOne.storm(), saveOne.value(), saveOne.dispatcher());
    }

    @Override
    public StormSaveOneRxStream<T> stream() {
        return new StormSaveOneRxStream<T>(this);
    }
}
