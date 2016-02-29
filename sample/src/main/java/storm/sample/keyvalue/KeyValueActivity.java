package storm.sample.keyvalue;

import ru.noties.debug.Debug;
import rx.functions.Action1;
import storm.sample.BaseActivity;
import storm.sample.DebugErrorAction;
import storm.sample.NoOpAction;

/**
 * Created by Dimitry Ivanov on 29.02.2016.
 */
public class KeyValueActivity extends BaseActivity {

    @Override
    public void showcase() {

        final StormKv kv = new StormKv(this);

        kv.get(String.class, "key_string")
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Debug.i("s: %s", s);
                        if (s == null) {
                            kv.put("key_string", "hello_there!!")
                                    .subscribe(new NoOpAction<Void>(), new DebugErrorAction());
                        }
                    }
                }, new DebugErrorAction());

        kv.get(KeyValueArbitraryData.class, "arbitrary_data")
                .subscribe(new Action1<KeyValueArbitraryData>() {
                    @Override
                    public void call(KeyValueArbitraryData keyValueArbitraryData) {
                        Debug.i("data: %s", keyValueArbitraryData);
                        if (keyValueArbitraryData == null) {
                            final KeyValueArbitraryData data = new KeyValueArbitraryData(14, .666D, null);
                            final KeyValueArbitraryData out = new KeyValueArbitraryData(1762, 218.D, data);
                            kv.put("arbitrary_data", out)
                                    .subscribe(new NoOpAction<Void>(), new DebugErrorAction());
                        }
                    }
                }, new DebugErrorAction());
    }

    static class KeyValueArbitraryData {

        final int someInt;
        final double someDouble;
        final KeyValueArbitraryData innerObject;

        KeyValueArbitraryData(int someInt, double someDouble, KeyValueArbitraryData innerObject) {
            this.someInt = someInt;
            this.someDouble = someDouble;
            this.innerObject = innerObject;
        }

        @Override
        public String toString() {
            return "KeyValueArbitraryData{" +
                    "someInt=" + someInt +
                    ", someDouble=" + someDouble +
                    ", innerObject=" + innerObject +
                    '}';
        }
    }
}
