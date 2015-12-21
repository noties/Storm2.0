package storm.sample;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import ru.noties.debug.Debug;
import ru.noties.debug.out.AndroidLogDebugOutput;
import rx.functions.Action1;
import storm.db.Database;
import storm.rx.StormRx;

/**
 * Created by Dimitry Ivanov on 19.12.2015.
 */
public class StormSampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(true));

        final StormRx storm = StormRx.newInstance(new Database.Configuration(
                getApplicationContext(),
                "test",
                1
        ));
        storm.registerTable(TestObject.class);

        storm.simpleQuery(TestObject.class, "sum(id)")
                .stream()
                .subscribeForUpdates()
                .asLong()
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Debug.i("simple query, sum(id): %s", aLong);
                    }
                });

        storm.query(TestObject.class)
                .stream()
                .subscribeForUpdates()
                .asList()
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
    }
}
