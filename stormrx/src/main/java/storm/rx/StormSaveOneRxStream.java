package storm.rx;

import rx.Observable;
import storm.core.Storm;
import storm.core.StormObject;
import storm.core.StormSaveOneDispatcher;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormSaveOneRxStream<T extends StormObject> implements StormRxStream {

    private final Storm mStorm;
    private final T mValue;
    private final StormSaveOneDispatcher mDispatcher;

    StormSaveOneRxStream(StormSaveOneRx<T> saveOne) {
        mStorm = saveOne.storm();
        mValue = saveOne.value();
        mDispatcher = saveOne.dispatcher();
    }

    public Observable<Long> create() {
        final StormRxObservable.ValueProvider<Long> provider = new StormRxObservable.ValueProvider<Long>() {
            @Override
            public Long provide() {
                return mDispatcher.save(mStorm, mValue);
            }
        };

        return StormRxObservable.createOneShot(provider);
    }
}
