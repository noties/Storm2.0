package storm.sample.versioning;

import java.util.List;

import ru.noties.debug.Debug;
import storm.core.Storm;
import storm.db.Database;
import storm.sample.BaseActivity;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
public class VersioningSampleActivity extends BaseActivity {

    @Override
    public void showcase() {

        final Storm storm1 = Storm.newInstance(new Database.Configuration(getApplicationContext(), "migration.db", 1))
                .registerTable(VersioningItem.class)
                .registerTable(VersioningNewTable.class);

        // all migration is done with annotations `@NewTable` & `@NewColumn`
        // they take only one argument - version when added

        // if we obtain now new storm instance passing db version as `2`
        // VersioningItem will be altered - new column `time` will be added
        // Will be created a new table `VersioningNewTable` with 2 columns (`id` & `data`)

        // if we obtain new storm instance with db version as `3`
        // `VersioningNewTable` will be altered - new column `newField` will be added

        // if we skip version 2 (previous db version was 1) & we are calling open with version `3`
        // VersioningItem will be altered - new column will be added
        // New table `VersioningNewTable` will be created with all the fields

        // if using apt, a static schema will be generated
        // for ex, for `VersioningItem` will be created a class named `VersioningItem_StormScheme` in the same package

        // to visualise lets see what generated class will return for 1, 2, 3 versions
        final VersioningItem_Scheme itemStormScheme = new VersioningItem_Scheme();
        final VersioningNewTable_Scheme table_stormScheme = new VersioningNewTable_Scheme();

        List<String> list;

        for (int i = 2; i <= 3; i++) {

            final int oldVersion = i - 1;
            final int newVersion = i;

            list = itemStormScheme.onUpgrade(oldVersion, newVersion);
            if (list != null && list.size() > 0) {
                for (String statement: list) {
                    Debug.i("Item, old: %d, new: %s, statement: %s", oldVersion, newVersion, statement);
                }
            } else {
                Debug.i("Item, old: %d, new: %d, has no upgrade statements", oldVersion, newVersion);
            }

            list = table_stormScheme.onUpgrade(oldVersion, newVersion);
            if (list != null && list.size() > 0) {
                for (String statement: list) {
                    Debug.i("Table, old: %d, new: %d, statement: %s", oldVersion, newVersion, statement);
                }
            } else {
                Debug.i("Table, old: %d, new: %d, has no upgrade statements", oldVersion, newVersion);
            }
        }

        // additionally lets check for 1 -> 3 migration

        list = itemStormScheme.onUpgrade(1, 3);
        if (list != null && list.size() > 0) {
            for (String statement: list) {
                Debug.i("Item, old: 1, new: 3, statement: %s", statement);
            }
        } else {
            Debug.i("Item, old: 1, new: 3, has no upgrade statements");
        }

        list = table_stormScheme.onUpgrade(1, 3);
        if (list != null && list.size() > 0) {
            for (String statement: list) {
                Debug.i("Table, old: 1, new: 3, statement: %s", statement);
            }
        } else {
            Debug.i("Table, old: 1, new: 3, has no upgrade statements");
        }
    }
}
