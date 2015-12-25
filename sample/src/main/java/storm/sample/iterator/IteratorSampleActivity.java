package storm.sample.iterator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.noties.debug.Debug;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import storm.core.StormPrefillDatabaseModule;
import storm.db.Database;
import storm.db.DatabaseModuleAdapter;
import storm.iterator.CursorIterator;
import storm.iterator.CursorIteratorCached;
import storm.query.Sorting;
import storm.rx.StormRx;
import storm.sample.BaseActivity;

/**
 * Created by Dimitry Ivanov on 25.12.2015.
 */
public class IteratorSampleActivity extends BaseActivity {

    private Subscription mSubscription;
    private IteratorAdapter mAdapter;
    private Handler mHandler;

    @Override
    public void showcase() {
        final ListView listView = new ListView(this);
        mAdapter = new IteratorAdapter(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);

        setContentView(listView);

        final File currentDbFile = getDatabasePath("iterator.db");
        if (currentDbFile != null
                && currentDbFile.exists()) {
            if (currentDbFile.delete()) {
                Debug.i("Deleted existing database file");
            }
        }

        final StormRx stormRx = StormRx.newInstance(new Database.Configuration(getApplicationContext(), "iterator.db", 2))
                .registerTable(IteratorItem.class);
        stormRx.registerDatabaseModule(new ReCreateOnUpgradeModule());
        stormRx.registerDatabaseModule(new StormPrefillDatabaseModule<IteratorItem>(stormRx, IteratorItem.class, new PreFillModule()));

        mSubscription = stormRx.query(IteratorItem.class)
                .orderBy("id", Sorting.DESC)
                .stream()
                .subscribeForUpdates() // loader functionality
                .asCachedIterator(30)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CursorIteratorCached<IteratorItem>>() {
                    @Override
                    public void call(CursorIteratorCached<IteratorItem> iteratorItems) {
                        Debug.i("Updated iterator, size: %d", iteratorItems.getCount());
                        mAdapter.setIterator(iteratorItems);
                    }
                });

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            final int mult = 10;

            int current;

            @Override
            public void run() {

                Debug.i("Generating new items");

                final List<IteratorItem> list = new ArrayList<IteratorItem>(mult);
                for (int i = 0; i < mult; i++) {
                    list.add(new IteratorItem().setData("New Item #" + (current + (i + 1))));
                }

                current += mult;

                Debug.i("Inserting new items");

                stormRx.save(list).stream().create().subscribe(new Action1<long[]>() {
                    @Override
                    public void call(long[] longs) {
                        Debug.i("New items inserted");
                    }
                });
                mHandler.postDelayed(this, 2000L);
            }
        }, 2000L);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }

        if (mAdapter != null
                && mAdapter.getIterator() != null) {
            mAdapter.getIterator().close();
        }
    }

    private static class IteratorAdapter extends ArrayAdapter<IteratorItem> {

        private CursorIterator<IteratorItem> mIterator;

        public IteratorAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {
            return mIterator != null ? mIterator.getCount() : 0;
        }

        @Override
        public IteratorItem getItem(int position) {
            return mIterator.get(position);
        }

        public void setIterator(CursorIterator<IteratorItem> iterator) {
            if (mIterator != null) {
                mIterator.close();
            }

            mIterator = iterator;
            notifyDataSetChanged();
        }

        CursorIterator<IteratorItem> getIterator() {
            return mIterator;
        }
    }

    private static class PreFillModule implements StormPrefillDatabaseModule.PrefillProvider<List<IteratorItem>> {

        @Override
        public List<IteratorItem> provide() {
            final int size = 10;
            final List<IteratorItem> list = new ArrayList<>(size);
            for (int i = 1; i <= size; i++) {
                list.add(new IteratorItem().setData("Item #" + i));
            }
            return list;
        }
    }

    private static class ReCreateOnUpgradeModule extends DatabaseModuleAdapter {

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IteratorItem");
            final IteratorItem_StormScheme scheme = new IteratorItem_StormScheme();
            for (String statement: scheme.onCreate()) {
                db.execSQL(statement);
            }
        }
    }
}
