package storm.core;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.Collection;

import storm.parser.StormParser;
import storm.parser.converter.StormConverter;
import storm.parser.metadata.StormMetadata;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
class StormSaveManyDispatcherImpl implements StormSaveManyDispatcher {

    @Override
    public <T extends StormObject> long[] save(Storm storm, Collection<T> collection) {

        if (collection == null) {
            throw new NullPointerException("Cannot save NULL collection");
        }

        final int size = collection.size();

        if (size == 0) {
            return new long[0];
        }

        //noinspection unchecked
        final Class<T> table = (Class<T>) collection.iterator().next().getClass();

        final StormParser<T> parser = storm.parser(table);

        final StormMetadata<T> metadata = storm.metadata(table, parser);
        final StormConverter<T> converter = storm.converter(table, parser);

        final String tableName = metadata.tableName();

        final SQLiteDatabase db = storm.database().open();

        final long[] out = new long[size];

        try {

            int index = 0;
            ContentValues cv;

            // at this point we already might have a transaction
            final boolean hasTransactionAlready = db.inTransaction();

            if (!hasTransactionAlready) {
                db.beginTransaction();
            }

            try {
                for (T value: collection) {

                    cv = converter.toContentValues(value, !metadata.isPrimaryKeyAutoincrement());
                    out[index++] = db.insert(tableName, null, cv);
                }

                if (!hasTransactionAlready) {
                    db.setTransactionSuccessful();
                }

                if (index > 0) {
                    storm.notifyChange(table);
                }

            } catch (SQLiteException e) {

                throw StormException.newInstance(
                        e,
                        "Exception during `saveMany` operation for a class: `%s`, values: `%s`",
                        table.getName(), collection
                );

            } finally {
                if (!hasTransactionAlready) {
                    db.endTransaction();
                }
            }


        } finally {
            storm.database().close();
        }

        return out;
    }
}
