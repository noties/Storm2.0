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
public class StormQueryRxStream<T extends StormObject> {

    private static final boolean DEF_ONE_SHOT = true;

    private final Storm mStorm;
    private final Class<T> mTable;
    private final Query mQuery;
    private final StormQueryDispatcher mDispatcher;

    StormQueryRxStream(StormQueryRx<T> query) {
        mStorm = query.storm();
        mTable = query.table();
        mQuery = query.query();
        mDispatcher = query.dispatcher();
    }

    public Observable<Cursor> asCursor() {
        return asCursor(DEF_ONE_SHOT);
    }

    public Observable<Cursor> asCursor(boolean oneShot) {

        final StormRxObservable.ValueProvider<Cursor> provider = new StormRxObservable.ValueProvider<Cursor>() {
            @Override
            public Cursor provide() {
                return mDispatcher.asCursor(mStorm, mQuery);
            }
        };

        if (oneShot) {
            return StormRxObservable.createOneShot(provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }

    public Observable<T> asOne() {
        return asOne(DEF_ONE_SHOT);
    }

    public Observable<T> asOne(boolean oneShot) {

        final StormRxObservable.ValueProvider<T> provider = new StormRxObservable.ValueProvider<T>() {
            @Override
            public T provide() {
                return mDispatcher.asOne(mStorm, mTable, mQuery);
            }
        };

        if (oneShot) {
            return StormRxObservable.createOneShot(provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }

    public Observable<List<T>> asList() {
        return asList(DEF_ONE_SHOT);
    }

    public Observable<List<T>> asList(boolean oneShot) {

        final StormRxObservable.ValueProvider<List<T>> provider = new StormRxObservable.ValueProvider<List<T>>() {
            @Override
            public List<T> provide() {
                return mDispatcher.asList(mStorm, mTable, mQuery);
            }
        };

        if (oneShot) {
            return StormRxObservable.createOneShot(provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }

    public Observable<CursorIterator<T>> asIterator() {
        return asIterator(DEF_ONE_SHOT);
    }

    public Observable<CursorIterator<T>> asIterator(boolean oneShot) {

        final StormRxObservable.ValueProvider<CursorIterator<T>> provider = new StormRxObservable.ValueProvider<CursorIterator<T>>() {
            @Override
            public CursorIterator<T> provide() {
                return mDispatcher.asIterator(mStorm, mTable, mQuery);
            }
        };

        if (oneShot) {
            return StormRxObservable.createOneShot(provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }

    public Observable<CursorIteratorCached<T>> asCachedIterator(int cacheSize) {
        return asCachedIterator(DEF_ONE_SHOT, cacheSize);
    }

    public Observable<CursorIteratorCached<T>> asCachedIterator(boolean oneShot, final int cacheSize) {

        final StormRxObservable.ValueProvider<CursorIteratorCached<T>> provider = new StormRxObservable.ValueProvider<CursorIteratorCached<T>>() {
            @Override
            public CursorIteratorCached<T> provide() {
                return mDispatcher.asCachedIterator(mStorm, mTable, mQuery, cacheSize);
            }
        };

        if (oneShot) {
            return StormRxObservable.createOneShot(provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }
}
