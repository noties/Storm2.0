package storm.sample.prefill;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.noties.debug.Debug;
import storm.core.Storm;
import storm.core.StormPrefillDatabaseModule;
import storm.db.Database;
import storm.sample.BaseActivity;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
public class PreFillSampleActivity extends BaseActivity {

    @Override
    public void showcase() {

        final Storm storm = Storm.newInstance(new Database.Configuration(getApplicationContext(), "pre-fill.db", 1));

        storm.registerTable(PrefillItem.class);

        // prefilling will be done only on database creation
        storm.registerDatabaseModule(new StormPrefillDatabaseModule<PrefillItem>(storm, PrefillItem.class, new StormPrefillDatabaseModule.PrefillProvider<List<PrefillItem>>() {
            @Override
            public List<PrefillItem> provide() {
                final int size = 3;
                final List<PrefillItem> items = new ArrayList<>(size);
                for (int i = 1; i <= size; i++) {
                    items.add(new PrefillItem("simple_key_" + i, String.valueOf(i)));
                }
                return items;
            }
        }));

        storm.registerDatabaseModule(new StormPrefillDatabaseModule<PrefillItem>(storm, PrefillItem.class, new StormPrefillDatabaseModule.PrefillProvider<List<PrefillItem>>() {
            @Override
            public List<PrefillItem> provide() {

                InputStream inputStream = null;

                try {
                    inputStream = getAssets().open("prefill_items.json");
                } catch (IOException e) {
                    Debug.e(e);
                }

                if (inputStream == null) {
                    //noinspection unchecked
                    return Collections.EMPTY_LIST;
                }

                final Gson gson = new Gson();
                final InputStreamReader reader = new InputStreamReader(inputStream);

                try {
                    final List<PrefillItem> list = gson.fromJson(new InputStreamReader(inputStream), new TypeToken<List<PrefillItem>>(){}.getType());
                    if (list == null) {
                        //noinspection unchecked
                        return Collections.EMPTY_LIST;
                    } else {
                        return list;
                    }
                } finally {
                    try {
                        reader.close();
                    } catch (IOException e) {}
                    try {
                        inputStream.close();
                    } catch (IOException e) {}
                }
            }
        }));

        // note that prefill will be executed in this call (if there is no such database still)
        // because Storm will call Database.open() before query

        Debug.i("Initialized Storm. Calling `query`");

        final List<PrefillItem> items = storm.query(PrefillItem.class).asList();

        Debug.i("Query returned: %s", items);

    }
}
