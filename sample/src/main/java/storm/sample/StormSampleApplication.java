package storm.sample;

import android.app.Application;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.noties.debug.Debug;
import ru.noties.debug.out.AndroidLogDebugOutput;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import storm.db.Database;
import storm.query.Selection;
import storm.rx.StormObservablePreprocessor;
import storm.rx.StormRx;

/**
 * Created by Dimitry Ivanov on 19.12.2015.
 */
public class StormSampleApplication extends Application {

    // samples todo
    // pre-fill sample
    // version migration
    // pseudo-joins
    // serializers (with Lazy fields)
    // custom table for parsing query with join
    // rx (with notificationSubscriptions)
    // iterator (with cached)

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(true));

        final File currentDbFile = getDatabasePath("test");
        if (currentDbFile != null
                && currentDbFile.exists()) {
            currentDbFile.delete();
        }

        final StormRx storm = StormRx.newInstance(new Database.Configuration(
                getApplicationContext(),
                "test",
                1
        ));
        storm.registerTable(TestObject.class);
        storm.registerObservablePreprocessor(new StormObservablePreprocessor() {
            @Override
            public <V> Observable<V> preProcess(Observable<V> observable) {
                return observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io());
            }
        });

        storm.simpleQuery(TestObject.class, "sum(id)")
                .stream()
                .subscribeForUpdates()
                .asLong()
                .debounce(50L, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Debug.i("simple query, sum(id): %s", aLong);
                    }
                });

        storm.count(TestObject.class)
                .stream()
                .subscribeForUpdates()
                .create()
                .debounce(50L, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Debug.i("count: %s", integer);
                    }
                });


        storm.query(TestObject.class)
                .stream()
                .subscribeForUpdates()
                .asList()
                .debounce(50L, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<List<TestObject>>() {
                    @Override
                    public void call(List<TestObject> testObjects) {
                        Debug.i("size: %s, testObjects: %s", (testObjects != null ? testObjects.size() : null), testObjects);
                    }
                });

        final long[] ids = storm.save(Arrays.asList(
                new TestObject().setData("first"),
                new TestObject().setData("second"),
                new TestObject().setData("third")
        )).execute();

        Debug.i("inserted: %s", Arrays.toString(ids));

        final long[] ids_2 = storm.save(Arrays.asList(
                new TestObject().setData("forth"),
                new TestObject().setData("fifth"),
                new TestObject().setData("sixth")
        )).execute();

        Debug.i("inserted 2: %s", Arrays.toString(ids_2));

        final List<TestObject> list = storm.query(TestObject.class).asList();
        Debug.i("list: %s", list);

        storm.save(new TestObject().setData("saveOne"))
                .stream()
                .create()
                .flatMap(new Func1<Long, Observable<TestObject>>() {
                    @Override
                    public Observable<TestObject> call(Long aLong) {
                        Debug.i("saved one, id: %s, thread: %s", aLong, Thread.currentThread());
                        return storm.query(TestObject.class, Selection.eq("id", aLong))
                                .stream()
                                .asOne();
                    }
                })
                .subscribe(new Action1<TestObject>() {
                    @Override
                    public void call(TestObject testObject) {
                        Debug.i("saveOne object: %s, thread: %s", testObject, Thread.currentThread());
                    }
                });

        storm.save(Arrays.asList(new TestObject().setData("many1"), new TestObject().setData("many2")))
                .stream()
                .create()
                .subscribe(new Action1<long[]>() {
                    @Override
                    public void call(long[] longs) {
                        Debug.i("savedMany: %s", Arrays.toString(longs));
                    }
                });

        storm.update(new TestObject().setId(1L).setData("updated first"))
                .stream()
                .create()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Debug.i("updated: %s", integer);
                    }
                });

        storm.update(Arrays.asList(new TestObject().setId(2L).setData("updated second"), new TestObject().setId(3L).setData("updated third")))
                .stream()
                .create()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Debug.i("updated many: %s", integer);
                    }
                });

        storm.fill(new TestObject().setData("filled"))
                .less("id", 6L)
                .and()
                .greater("id", 3L)
                .stream()
                .create()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Debug.i("filled: %s", integer);
                    }
                });

        storm.delete(TestObject.class, Selection.eq("id", 6L))
                .stream()
                .create()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Debug.i("deleted: %s", integer);

                        storm.deleteAll(TestObject.class)
                                .stream()
                                .create()
                                .subscribe(new Action1<Integer>() {
                                    @Override
                                    public void call(Integer integer) {
                                        Debug.i("deleted all: %s", integer);
                                    }
                                });
                    }
                });
    }
}
