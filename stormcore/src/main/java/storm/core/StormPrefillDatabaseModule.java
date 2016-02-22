package storm.core;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.List;

import storm.db.DatabaseModuleAdapter;
import storm.parser.StormParser;
import storm.parser.converter.StormConverter;
import storm.parser.metadata.StormMetadata;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
public class StormPrefillDatabaseModule<T extends StormObject> extends DatabaseModuleAdapter {

    public interface PrefillProvider<T> {
        T provide();
    }

    private final Storm mStorm;
    private final Class<T> mTable;
    private final PrefillProvider<List<T>> mProvider;

    public StormPrefillDatabaseModule(Storm storm, Class<T> table, PrefillProvider<List<T>> provider) {
        this.mStorm = storm;
        this.mTable = table;
        this.mProvider = provider;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final StormParser<T> parser         = mStorm.parser(mTable);
        final StormMetadata<T> metadata     = mStorm.metadata(mTable, parser);
        final StormConverter<T> converter   = mStorm.converter(mTable, parser);


        final String tableName = metadata.tableName();
        final boolean putPrimaryKey = !metadata.isPrimaryKeyAutoincrement();

        final StormTransactionController transactionController = new StormTransactionController(db);
        transactionController.beginTransaction();

        try {

            // unfortunately we cannot use storm.save()
            // because at this point we are still initializing database
            // so we have to execute insert statements by our selves

            for (T item: mProvider.provide()) {
                db.insert(tableName, null, converter.toContentValues(item, putPrimaryKey));
            }

            transactionController.setTransactionSuccessful();

        } catch (SQLiteException e) {
            throw new StormException(e);
        } finally {
            transactionController.endTransaction();
        }
    }
}
