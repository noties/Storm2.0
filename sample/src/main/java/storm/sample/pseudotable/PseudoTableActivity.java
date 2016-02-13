package storm.sample.pseudotable;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.noties.debug.Debug;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import storm.db.Database;
import storm.query.Query;
import storm.rx.StormObservablePreprocessor;
import storm.rx.StormRx;
import storm.sample.BaseActivity;

/**
 * Created by Dimitry Ivanov on 08.02.2016.
 */
public class PseudoTableActivity extends BaseActivity {

    @Override
    public void showcase() {

        final StormRx stormRx = StormRx.newInstance(new Database.Configuration(
                getApplicationContext(),
                "pseudo_table",
                1
        ));

        stormRx.registerTable(PseudoMainInfo.class);
        stormRx.registerTable(PseudoSalary.class);

        stormRx.registerObservablePreprocessor(new StormObservablePreprocessor() {
            @Override
            public <V> Observable<V> preProcess(Observable<V> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        });

        stormRx.deleteAll(PseudoMainInfo.class).execute();
        stormRx.deleteAll(PseudoSalary.class).execute();

        final Query query = new Query()
                .raw("SELECT PseudoMainInfo.*, sum(amount) as amount FROM PseudoMainInfo JOIN PseudoSalary ON PseudoMainInfo.id = mainInfoId GROUP BY PseudoMainInfo.id");
//                .select(PseudoMainInfo_Metadata.TABLE_NAME + ".*", "sum(" + PseudoSalary_Metadata.COL_AMOUNT + ")")
//                .from(PseudoMainInfo_Metadata.TABLE_NAME)
//                .join(PseudoSalary_Metadata.TABLE_NAME, new Selection().raw(PseudoMainInfo_Metadata.TABLE_NAME + "." + PseudoMainInfo_Metadata.COL_ID + " = " + PseudoSalary_Metadata.COL_MAIN_INFO_ID))
//                .groupBy(PseudoMainInfo_Metadata.TABLE_NAME + "." + PseudoMainInfo_Metadata.COL_ID, null);

        Debug.i("query: %s, %s", query.getStatement(), query.getArguments());

        // to subscribe on salary update?
        stormRx.query(PseudoResult.class, query)
                .stream()
                .subscribeForUpdates()
                .asList()
                .subscribe(new Action1<List<PseudoResult>>() {
                    @Override
                    public void call(List<PseudoResult> pseudoResults) {
                        Debug.i("results: %s", pseudoResults);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Debug.e(throwable);
                    }
                });

        final List<PseudoMainInfo> list = Arrays.asList(
                new PseudoMainInfo(1L, "First"),
                new PseudoMainInfo(2L, "Second"),
                new PseudoMainInfo(3L, "Third"),
                new PseudoMainInfo(4L, "Forth"),
                new PseudoMainInfo(5L, "Fifth")
        );

        stormRx.save(list).execute();

        final double base = 1000.D;
        final int months = 12;
        final double ratio = .15D;
        final int count = list.size();

        final Handler handler = new Handler();
        handler.post(new Runnable() {

            int runs;

            @Override
            public void run() {

                if (!isVisible()) {
                    return;
                }

                final List<PseudoSalary> salaries = new ArrayList<PseudoSalary>();

                for (int i = 0; i < count; i++) {
                    salaries.add(new PseudoSalary(
                            i + 1,
                            runs,
                            (i + 1) * base * ratio
                    ));
                }

                stormRx.save(salaries)
                        .stream()
                        .create()
                        .subscribe(new Action1<long[]>() {
                            @Override
                            public void call(long[] longs) {

                            }
                        });

                if (++runs >= months) {
                    return;
                }

                handler.postDelayed(this, 2000L);
            }
        });
    }
}
