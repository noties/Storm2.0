package storm.sample.serialize;

import ru.noties.debug.Debug;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import storm.db.Database;
import storm.lazy.Lazy;
import storm.query.Selection;
import storm.rx.StormObservablePreprocessor;
import storm.rx.StormRx;
import storm.sample.BaseActivity;
import storm.sample.DebugErrorAction;

/**
 * Created by Dimitry Ivanov on 13.02.2016.
 */
public class SerializeActivity extends BaseActivity {
    @Override
    public void showcase() {

        final StormRx stormRx = StormRx.newInstance(new Database.Configuration(
                getApplicationContext(),
                "serialize.db",
                1
        ));

        stormRx.registerTable(SerializeItem.class);
        stormRx.registerObservablePreprocessor(new StormObservablePreprocessor() {
            @Override
            public <V> Observable<V> preProcess(Observable<V> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        });

        final SerializeItem item = new SerializeItem()
                .setIntBool(true)
                .setStrBool(true)
                .setSimpleJson(new SerializeJsonItem(45L, "text_text_text"))
                .setLazyJson(new Lazy<SerializeJsonItem>(new Lazy.LazyProvider<SerializeJsonItem>() {
                    @Override
                    public SerializeJsonItem provide() {
                        return new SerializeJsonItem(11L, "lazy_lazy_lazy");
                    }
                }));

        stormRx.save(item)
                .stream()
                .create()
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (aLong != null) {
                            stormRx.query(SerializeItem.class, Selection.eq(SerializeItem_Metadata.COL_ID, aLong))
                                    .stream()
                                    .asOne()
                                    .subscribe(new Action1<SerializeItem>() {
                                        @Override
                                        public void call(SerializeItem serializeItem) {
                                            final Lazy<SerializeJsonItem> lazy = serializeItem.getLazyJson();
                                            Debug.i("id: %d, intBool: %s, strBool: %s, json: %s, lazyJson(isProviderCalled: %s): %s",
                                                    serializeItem.getId(),
                                                    serializeItem.isIntBool(),
                                                    serializeItem.isStrBool(),
                                                    serializeItem.getSimpleJson(),
                                                    lazy.isProviderCalled(),
                                                    lazy.get()
                                            );
                                        }
                                    }, new DebugErrorAction());
                        }
                    }
                }, new DebugErrorAction());
    }
}
