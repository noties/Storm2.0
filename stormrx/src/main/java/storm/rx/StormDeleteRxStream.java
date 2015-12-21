package storm.rx;

import rx.Observable;
import storm.core.StormDeleteDispatcher;
import storm.core.StormObject;
import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormDeleteRxStream<T extends StormObject> implements StormRxStreamSingleValue<Integer> {

    private final StormRx mStorm;
    private final Class<T> mTable;
    private final Selection mSelection;
    private final StormDeleteDispatcher mDispatcher;

    StormDeleteRxStream(StormDeleteRxType<T> type) {
        mStorm = type.storm();
        mTable = type.table();
        mSelection = type.selection();
        mDispatcher = type.dispatcher();
    }

    @Override
    public Observable<Integer> create() {

        final StormRxObservable.ValueProvider<Integer> provider = new StormRxObservable.ValueProvider<Integer>() {
            @Override
            public Integer provide() {
                return mDispatcher.execute(mStorm, mTable, mSelection);
            }
        };

        return StormRxObservable.createOneShot(mStorm, provider);
    }
}
