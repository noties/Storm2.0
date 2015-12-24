package storm.sample.rx;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.noties.debug.Debug;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import storm.db.Database;
import storm.query.Selection;
import storm.rx.StormObservablePreprocessor;
import storm.rx.StormRx;
import storm.sample.BaseActivity;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
public class RxSampleActivity extends BaseActivity {

    private static final String DB_NAME = "rx-sample.db";

    @Override
    public void showcase() {

        final File currentDbFile = getDatabasePath(DB_NAME);
        if (currentDbFile != null
                && currentDbFile.exists()) {
            if (currentDbFile.delete()) {
                Debug.i("Deleted existing database file");
            }
        }

        final StormRx storm = StormRx.newInstance(new Database.Configuration(getApplicationContext(), DB_NAME, 1));
        storm.registerTable(RxSampleItem.class);

        // we could register `preprocessor` for every observable that StormRx will produce
        // here we are setting schedulers in order to not provide it for every call
        // Note, that if any additional operations will be applied to emitted Observable
        // schedulers must be initialized manually
        storm.registerObservablePreprocessor(new StormObservablePreprocessor() {
            @Override
            public <V> Observable<V> preProcess(Observable<V> observable) {
                return observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io());
            }
        });

        // It's a great simple Loader alternative
        // The first time query will be proceed as usual
        // After that subscription stream will subscribe for database updates
        // One doesn't have to manupulate databse via StormRx in order to receive notifications
        // Every table has a `notificationUri` parameter, just call ContentResolver.notifyChange with this uri value
        //
        // Also we are applying `debounce` to the stream due to possible multiple notifications
        storm.simpleQuery(RxSampleItem.class, "sum(id)")
                .stream()
                .subscribeForUpdates() // simple loader alternative
                .asLong()
                .debounce(50L, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Debug.i("Simple query, sum(id): %s", aLong);
                    }
                });

        // This method counts total items in table & subscribes for updates
        storm.count(RxSampleItem.class)
                .stream()
                .subscribeForUpdates()
                .create()
                .debounce(50L, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Debug.i("Count: %s", integer);
                    }
                });


        storm.query(RxSampleItem.class)
                .stream()
                .subscribeForUpdates()
                .asList()
                .debounce(50L, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<List<RxSampleItem>>() {
                    @Override
                    public void call(List<RxSampleItem> rxSampleItems) {
                        Debug.i("Size: %s, Objects: %s", (rxSampleItems != null ? rxSampleItems.size() : null), rxSampleItems);
                    }
                });

        final long[] ids = storm.save(Arrays.asList(
                new RxSampleItem().setData("first"),
                new RxSampleItem().setData("second"),
                new RxSampleItem().setData("third")
        )).execute();

        Debug.i("Sync inserted: %s", Arrays.toString(ids));

        final long[] ids_2 = storm.save(Arrays.asList(
                new RxSampleItem().setData("forth"),
                new RxSampleItem().setData("fifth"),
                new RxSampleItem().setData("sixth")
        )).execute();

        Debug.i("Sync inserted 2: %s", Arrays.toString(ids_2));

        final List<RxSampleItem> list = storm.query(RxSampleItem.class).asList();
        Debug.i("Sync query, list: %s", list);

        storm.save(new RxSampleItem().setData("saveOne"))
                .stream()
                .create()
                .flatMap(new Func1<Long, Observable<RxSampleItem>>() {
                    @Override
                    public Observable<RxSampleItem> call(Long aLong) {
                        Debug.i("Saved one, id: %s", aLong);
                        return storm.query(RxSampleItem.class, Selection.eq("id", aLong))
                                .stream()
                                .asOne();
                    }
                })
                .subscribe(new Action1<RxSampleItem>() {
                    @Override
                    public void call(RxSampleItem rxSampleItem) {
                        Debug.i("Save one object: %s", rxSampleItem);
                    }
                });

        storm.save(Arrays.asList(new RxSampleItem().setData("many1"), new RxSampleItem().setData("many2")))
                .stream()
                .create()
                .subscribe(new Action1<long[]>() {
                    @Override
                    public void call(long[] longs) {
                        Debug.i("Saved many, ids: %s", Arrays.toString(longs));
                    }
                });

        storm.update(new RxSampleItem().setId(1L).setData("updated first"))
                .stream()
                .create()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Debug.i("Updated items count: %s", integer);
                    }
                });

        storm.update(Arrays.asList(new RxSampleItem().setId(2L).setData("updated second"), new RxSampleItem().setId(3L).setData("updated third")))
                .stream()
                .create()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Debug.i("Updated many count: %s", integer);
                    }
                });

        storm.fill(new RxSampleItem().setData("filled"))
                .where() // this call is completely optional, but it's good to have it for readability
                .less("id", 6L)
                .and()
                .greater("id", 3L)
                .stream()
                .create()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Debug.i("Filled count: %s", integer);
                    }
                });

        storm.delete(RxSampleItem.class, Selection.eq("id", 6L))
                .stream()
                .create()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Debug.i("Deleted count: %s", integer);

                        storm.deleteAll(RxSampleItem.class)
                                .stream()
                                .create()
                                .subscribe(new Action1<Integer>() {
                                    @Override
                                    public void call(Integer integer) {
                                        Debug.i("Deleted all count: %s", integer);
                                    }
                                });
                    }
                });
    }
}
