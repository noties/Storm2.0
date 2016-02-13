package storm.sample.extreme;

import java.util.ArrayList;
import java.util.List;

import ru.noties.debug.Debug;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import storm.db.Database;
import storm.rx.StormObservablePreprocessor;
import storm.rx.StormRx;
import storm.sample.BaseActivity;

/**
 * Created by Dimitry Ivanov on 13.02.2016.
 */
public class ExtremeActivity extends BaseActivity {

    @Override
    public void showcase() {

        final StormRx stormRx = StormRx.newInstance(new Database.Configuration(
                getApplicationContext(),
                "extreme.db",
                1
        ));

        stormRx.registerTable(ExtremeItem.class);
        stormRx.registerObservablePreprocessor(new StormObservablePreprocessor() {
            @Override
            public <V> Observable<V> preProcess(Observable<V> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        });

        final int[] rounds = new int[] { 1000, 10000, 100000 };

        for (final int round: rounds) {

            stormRx.deleteAll(ExtremeItem.class).execute();

            // generate items
            final List<ExtremeItem> list = generateItems(round);

            final long start = System.currentTimeMillis();

            stormRx.save(list)
                    .stream()
                    .create()
                    .subscribe(new Action1<long[]>() {
                        @Override
                        public void call(long[] longs) {
                            final long end = System.currentTimeMillis();
                            Debug.i("round: %d, took: %d ms, ids.length: %d", round, (end - start), (longs != null ? longs.length : null));
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Debug.e(throwable);
                        }
                    });
        }
    }

    private static List<ExtremeItem> generateItems(int count) {
        final List<ExtremeItem> list = new ArrayList<>(count);
        String value;
        for (int i = 0; i < count; i++) {
            value = String.valueOf(i + 1);
            list.add(new ExtremeItem(value, value, value));
        }
        return list;
    }
}
