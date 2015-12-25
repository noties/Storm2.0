package storm.rx;

import java.util.List;

import rx.Observable;
import storm.core.StormFillDispatcher;
import storm.core.StormObject;
import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormFillRxStream<T extends StormObject> implements StormRxStreamSingleValue<Integer> {

    private final StormRx mStorm;
    private final Selection mSelection;
    private final List<String> mColumns;
    private final boolean mIsInclude;
    private final T mValue;
    private final StormFillDispatcher mDispatcher;

    StormFillRxStream(StormFillRx<T> fill) {
        mStorm = fill.storm();
        mSelection = fill.selection();
        mColumns = fill.columns();
        mIsInclude = fill.isInclude();
        mValue = fill.value();
        mDispatcher = fill.dispatcher();
    }

    @Override
    public Observable<Integer> create() {

        final StormRxObservable.ValueProvider<Integer> provider = new StormRxObservable.ValueProvider<Integer>() {
            @Override
            public Integer provide() {
                return mDispatcher.fill(mStorm, mSelection, mColumns, mIsInclude, mValue);
            }
        };

        return StormRxObservable.createOneShot(mStorm, provider);
    }
}
