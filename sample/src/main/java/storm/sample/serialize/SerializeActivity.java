package storm.sample.serialize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                3
        ));

        stormRx.registerTable(SerializeItem.class);
        stormRx.registerObservablePreprocessor(new StormObservablePreprocessor() {
            @Override
            public <V> Observable<V> preProcess(Observable<V> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        });

        final List<List<String>> list = new ArrayList<List<String>>();
        list.add(Arrays.asList("1_2", "1_2"));
        list.add(Arrays.asList("2_1", "2_2"));

        final Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        map.put("key1", Arrays.asList(1, 2, 3));

        final SerializeItem item = new SerializeItem()
                .setIntBool(true)
                .setStrBool(true)
                .setSimpleJson(new SerializeJsonItem(45L, "text_text_text"))
                .setObject(new SerializeJsonItem(666L, "generic_deserialization & runtime type query"))
                .setLazyJson(new Lazy<SerializeJsonItem>(new Lazy.LazyProvider<SerializeJsonItem>() {
                    @Override
                    public SerializeJsonItem provide() {
                        return new SerializeJsonItem(11L, "lazy_lazy_lazy");
                    }
                }))
                .setListListString(list)
                .setLazyMap(new Lazy<Map<String, List<Integer>>>(new Lazy.LazyProvider<Map<String, List<Integer>>>() {
                    @Override
                    public Map<String, List<Integer>> provide() {
                        return map;
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

                                            Debug.i("list: %s", serializeItem.getListListString());
                                            Debug.i("map: %s", serializeItem.getLazyMap().get());

                                        }
                                    }, new DebugErrorAction());
                        }
                    }
                }, new DebugErrorAction());
    }
}
