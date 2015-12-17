package storm.core;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.Collection;

import storm.parser.StormParser;

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

                    // todo also, fetch if `isAutoincrement` to not include in ContentValues
                    cv = parser.toContentValues(value, true);
                    out[index++] = db.insert(null, null, cv);
                }

                if (!hasTransactionAlready) {
                    db.setTransactionSuccessful();
                }

            } catch (SQLiteException e) {

                throw new RuntimeException(e);

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
