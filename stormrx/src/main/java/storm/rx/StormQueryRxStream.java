package storm.rx;

import android.database.Cursor;

import java.util.List;

import rx.Observable;
import storm.core.Storm;
import storm.core.StormObject;
import storm.core.StormQueryDispatcher;
import storm.iterator.CursorIterator;
import storm.iterator.CursorIteratorCached;
import storm.query.Query;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormQueryRxStream<T extends StormObject> implements StormRxStreamWithUpdates {

    private final Storm mStorm;
    private final Class<T> mTable;
    private final Query mQuery;
    private final StormQueryDispatcher mDispatcher;

    private boolean mOneShot;

    StormQueryRxStream(StormQueryRx<T> query) {
        mStorm = query.storm();
        mTable = query.table();
        mQuery = query.query();
        mDispatcher = query.dispatcher();
        mOneShot = true;
    }

    @Override
    public StormQueryRxStream<T> subscribeForUpdates() {
        this.mOneShot = false;
        return this;
    }

    public Observable<Cursor> asCursor() {
        final StormRxObservable.ValueProvider<Cursor> provider = new StormRxObservable.ValueProvider<Cursor>() {
            @Override
            public Cursor provide() {
                return mDispatcher.asCursor(mStorm, mQuery);
            }
        };

        if (mOneShot) {
            return StormRxObservable.createOneShot(provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }

    public Observable<T> asOne() {

        final StormRxObservable.ValueProvider<T> provider = new StormRxObservable.ValueProvider<T>() {
            @Override
            public T provide() {
                return mDispatcher.asOne(mStorm, mTable, mQuery);
            }
        };

        if (mOneShot) {
            return StormRxObservable.createOneShot(provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }

    public Observable<List<T>> asList() {

        final StormRxObservable.ValueProvider<List<T>> provider = new StormRxObservable.ValueProvider<List<T>>() {
            @Override
            public List<T> provide() {
                return mDispatcher.asList(mStorm, mTable, mQuery);
            }
        };

        if (mOneShot) {
            return StormRxObservable.createOneShot(provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }

    public Observable<CursorIterator<T>> asIterator() {

        final StormRxObservable.ValueProvider<CursorIterator<T>> provider = new StormRxObservable.ValueProvider<CursorIterator<T>>() {
            @Override
            public CursorIterator<T> provide() {
                return mDispatcher.asIterator(mStorm, mTable, mQuery);
            }
        };

        if (mOneShot) {
            return StormRxObservable.createOneShot(provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }

    public Observable<CursorIteratorCached<T>> asCachedIterator(final int cacheSize) {

        final StormRxObservable.ValueProvider<CursorIteratorCached<T>> provider = new StormRxObservable.ValueProvider<CursorIteratorCached<T>>() {
            @Override
            public CursorIteratorCached<T> provide() {
                return mDispatcher.asCachedIterator(mStorm, mTable, mQuery, cacheSize);
            }
        };

        if (mOneShot) {
            return StormRxObservable.createOneShot(provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }
}
