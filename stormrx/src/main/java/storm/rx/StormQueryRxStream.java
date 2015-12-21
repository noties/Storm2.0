package storm.rx;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;
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
        return createObservable(mStorm.notificationUri(mTable), new ValueProvider<Cursor>() {
            @Override
            public Cursor provide() {
                return mDispatcher.asCursor(mStorm, mQuery);
            }
        });
    }

    public Observable<T> asOne() {
        return createObservable(mStorm.notificationUri(mTable), new ValueProvider<T>() {
            @Override
            public T provide() {
                return mDispatcher.asOne(mStorm, mTable, mQuery);
            }
        });
    }

    public Observable<List<T>> asList() {
        return createObservable(mStorm.notificationUri(mTable), new ValueProvider<List<T>>() {
            @Override
            public List<T> provide() {
                return mDispatcher.asList(mStorm, mTable, mQuery);
            }
        });
    }

    public Observable<CursorIterator<T>> asIterator() {
        return createObservable(mStorm.notificationUri(mTable), new ValueProvider<CursorIterator<T>>() {
            @Override
            public CursorIterator<T> provide() {
                return mDispatcher.asIterator(mStorm, mTable, mQuery);
            }
        });
    }

    public Observable<CursorIteratorCached<T>> asCachedIterator(final int cacheSize) {
        return createObservable(mStorm.notificationUri(mTable), new ValueProvider<CursorIteratorCached<T>>() {
            @Override
            public CursorIteratorCached<T> provide() {
                return mDispatcher.asCachedIterator(mStorm, mTable, mQuery, cacheSize);
            }
        });
    }

    private <V> Observable<V> createObservable(final Uri uri, final ValueProvider<V> provider) {
        return Observable.defer(new Func0<Observable<V>>() {
            @Override
            public Observable<V> call() {
                return Observable.create(new Observable.OnSubscribe<V>() {
                    @Override
                    public void call(final Subscriber<? super V> subscriber) {

                        final ContentObserver observer = new ContentObserver(new Handler()) {
                            @Override
                            public void onChange(boolean selfChange) {

                                if (!subscriber.isUnsubscribed()) {

                                    try {
                                        subscriber.onNext(provider.provide());
                                    } catch (Throwable t) {
                                        subscriber.onError(t);
                                        subscriber.onCompleted();
                                    }

                                } else {
                                    mStorm.database().unregisterContentObserver(this);
                                }

                            }
                        };

                        mStorm.database().registerContentObserver(uri, observer);
                    }
                }).startWith(provider.provide());
            }
        });
    }

    private interface ValueProvider<V> {
        V provide();
    }
}
