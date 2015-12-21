package storm.rx;

import android.database.ContentObserver;
import android.net.Uri;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;
import storm.core.Storm;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
class StormRxObservable {

    private StormRxObservable() {}

    interface ValueProvider<V> {
        V provide();
    }

    static <V> Observable<V> createOneShot(StormRx storm, final ValueProvider<V> provider) {
        final Observable<V> observable = Observable.defer(new Func0<Observable<V>>() {
            @Override
            public Observable<V> call() {
                return Observable.create(new Observable.OnSubscribe<V>() {
                    @Override
                    public void call(Subscriber<? super V> subscriber) {

                        if (subscriber.isUnsubscribed()) {
                            return;
                        }

                        try {
                            subscriber.onNext(provider.provide());
                        } catch (Throwable t) {
                            subscriber.onError(t);
                        } finally {
                            subscriber.onCompleted();
                        }
                    }
                });
            }
        });
        return storm.observablePreProcessor().preProcess(observable);
    }

    static <V, T extends StormObject> Observable<V> createStream(final StormRx storm, final Class<T> table, final ValueProvider<V> provider) {
        final Observable<V> observable = Observable.defer(new Func0<Observable<V>>() {
            @Override
            public Observable<V> call() {
                return Observable.create(new Observable.OnSubscribe<V>() {
                    @Override
                    public void call(final Subscriber<? super V> subscriber) {

                        if (subscriber.isUnsubscribed()) {
                            return;
                        }

                        final ContentObserver observer = new ContentObserver(null) {
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
                                    storm.database().unregisterContentObserver(this);
                                }

                            }
                        };

                        final Uri uri = storm.notificationUri(table);
                        storm.database().registerContentObserver(uri, observer);
                    }
                }).startWith(provider.provide());
            }
        });
        return storm.observablePreProcessor().preProcess(observable);
    }
}
