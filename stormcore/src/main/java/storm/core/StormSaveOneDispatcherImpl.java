package storm.core;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import storm.parser.StormParser;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
class StormSaveOneDispatcherImpl implements StormSaveOneDispatcher {

    @Override
    public <T extends StormObject> long save(Storm storm, T value) {

        if (value == null) {
            throw new NullPointerException("Cannot save NULL value");
        }

        //noinspection unchecked
        final Class<T> table = (Class<T>) value.getClass();
        final StormParser<T> parser = storm.parser(table);
        final ContentValues cv = parser.toContentValues(value, true); // todo, check if ok

        final SQLiteDatabase db = storm.database().open();

        try {

            return db.insert(
                    null,
                    null,
                    cv
            );

        } finally {
            storm.database().close();
        }
    }
}
