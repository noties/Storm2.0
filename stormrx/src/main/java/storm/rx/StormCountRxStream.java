package storm.rx;

import rx.Observable;
import storm.core.StormCountDispatcher;
import storm.core.StormObject;
import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormCountRxStream<T extends StormObject> implements StormRxStreamWithUpdates {

    private final StormRx mStorm;
    private final Class<T> mTable;
    private final Selection mSelection;
    private final StormCountDispatcher mDispatcher;

    private boolean mOneShot;

    StormCountRxStream(StormCountRx<T> count) {
        mStorm = count.storm();
        mTable = count.table();
        mSelection = count.selection();
        mDispatcher = count.dispatcher();

        mOneShot = true;
    }

    @Override
    public StormCountRxStream<T> subscribeForUpdates() {
        mOneShot = false;
        return this;
    }

    public Observable<Integer> create() {

        final StormRxObservable.ValueProvider<Integer> provider = new StormRxObservable.ValueProvider<Integer>() {
            @Override
            public Integer provide() {
                return mDispatcher.execute(mStorm, mTable, mSelection);
            }
        };
        
        if (mOneShot) {
            return StormRxObservable.createOneShot(mStorm, provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }
}
