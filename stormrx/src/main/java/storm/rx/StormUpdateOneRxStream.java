package storm.rx;

import rx.Observable;
import storm.core.StormObject;
import storm.core.StormUpdateOneDispatcher;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormUpdateOneRxStream<T extends StormObject> implements StormRxStream {

    private final StormRx mStorm;
    private final T mValue;
    private final StormUpdateOneDispatcher mDispatcher;

    StormUpdateOneRxStream(StormUpdateOneRx<T> updateOne) {
        mStorm = updateOne.storm();
        mValue = updateOne.value();
        mDispatcher = updateOne.dispatcher();
    }

    public Observable<Integer> create() {

        final StormRxObservable.ValueProvider<Integer> provider = new StormRxObservable.ValueProvider<Integer>() {
            @Override
            public Integer provide() {
                return mDispatcher.update(mStorm, mValue);
            }
        };

        return StormRxObservable.createOneShot(mStorm, provider);
    }
}
