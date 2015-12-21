package storm.rx;

import java.util.Collection;

import rx.Observable;
import storm.core.StormObject;
import storm.core.StormUpdateManyDispatcher;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormUpdateManyRxStream<T extends StormObject> implements StormRxStreamSingleValue<Integer> {

    private final StormRx mStorm;
    private final Collection<T> mValues;
    private final StormUpdateManyDispatcher mDispatcher;

    StormUpdateManyRxStream(StormUpdateManyRx<T> updateMany) {
        mStorm = updateMany.storm();
        mValues = updateMany.values();
        mDispatcher = updateMany.dispatcher();
    }

    @Override
    public Observable<Integer> create() {

        final StormRxObservable.ValueProvider<Integer> provider = new StormRxObservable.ValueProvider<Integer>() {
            @Override
            public Integer provide() {
                return mDispatcher.update(mStorm, mValues);
            }
        };

        return StormRxObservable.createOneShot(mStorm, provider);
    }
}
