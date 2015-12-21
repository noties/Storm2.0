package storm.rx;

import java.util.Collection;

import rx.Observable;
import storm.core.StormObject;
import storm.core.StormSaveManyDispatcher;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormSaveManyRxStream<T extends StormObject> implements StormRxStreamSingleValue<long[]> {

    private final StormRx mStorm;
    private final Collection<T> mValues;
    private final StormSaveManyDispatcher mDispatcher;

    StormSaveManyRxStream(StormSaveManyRx<T> saveMany) {
        mStorm = saveMany.storm();
        mValues = saveMany.values();
        mDispatcher = saveMany.dispatcher();
    }

    @Override
    public Observable<long[]> create() {

        final StormRxObservable.ValueProvider<long[]> provider = new StormRxObservable.ValueProvider<long[]>() {
            @Override
            public long[] provide() {
                return mDispatcher.save(mStorm, mValues);
            }
        };

        return StormRxObservable.createOneShot(mStorm, provider);
    }
}
