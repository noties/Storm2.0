package storm.sample.keyvalue;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import storm.db.Database;
import storm.query.Selection;
import storm.rx.StormObservablePreprocessor;
import storm.rx.StormRx;

/**
 * Created by Dimitry Ivanov on 28.02.2016.
 */
public class StormKv {

    private final StormRx mStormRx;
    private final Gson mGson;

    public StormKv(Context context) {
        this.mStormRx = StormRx.newInstance(new Database.Configuration(context, "key_value_table", 1));
        this.mStormRx.registerTable(StormKvItem.class);
        this.mStormRx.registerObservablePreprocessor(new StormObservablePreprocessor() {
            @Override
            public <V> Observable<V> preProcess(Observable<V> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        });
        this.mGson = new Gson();
    }

    public Observable<Void> put(@NonNull final String key, @Nullable final Object value) {

        final Selection selection = Selection.eq(StormKvItem_Metadata.COL_KEY, key);

        // if value is NULL, then remove
        if (value == null) {
            return mStormRx.delete(StormKvItem.class, selection)
                    .stream()
                    .create()
                    .flatMap(new VoidFlatMap<Integer>());
        }

        // detect if there is a value stored with this key
        return Observable.defer(new Func0<Observable<Void>>() {
            @Override
            public Observable<Void> call() {
                final String out = mGson.toJson(value);
                final StormKvItem item = new StormKvItem(key, out);
                final int count = mStormRx.count(StormKvItem.class, selection).execute();
                if (count == 0) {
                    return mStormRx.save(item)
                            .stream()
                            .create()
                            .flatMap(new VoidFlatMap<Long>());
                } else {
                    return mStormRx.update(item)
                            .stream()
                            .create()
                            .flatMap(new VoidFlatMap<Integer>());
                }
            }
        });
    }

    public <T> Observable<T> get(@NonNull final Class<T> type, @NonNull final String key) {
        return Observable.defer(new Func0<Observable<T>>() {
            @Override
            public Observable<T> call() {
                final Selection selection = Selection.eq(StormKvItem_Metadata.COL_KEY, key);
                return mStormRx.query(StormKvItem.class, selection)
                        .stream()
                        .asOne()
                        .flatMap(new Func1<StormKvItem, Observable<T>>() {
                            @Override
                            public Observable<T> call(StormKvItem stormKvItem) {
                                final String value = stormKvItem != null ? stormKvItem.getValue() : null;
                                if (TextUtils.isEmpty(value)) {
                                    return Observable.just(null);
                                }
                                final T out = mGson.fromJson(value, type);
                                return Observable.just(out);
                            }
                        });
            }
        });
    }



    private static class VoidFlatMap<T> implements Func1<T, Observable<Void>> {
        @Override
        public Observable<Void> call(T t) {
            return Observable.just(null);
        }
    }
}
