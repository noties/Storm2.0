package storm.core;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import storm.parser.StormParser;
import storm.parser.converter.StormConverter;
import storm.parser.metadata.StormMetadata;

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
        final StormMetadata<T> metadata = storm.metadata(table, parser);
        final StormConverter<T> converter = storm.converter(table, parser);

        final ContentValues cv = converter.toContentValues(value, !metadata.isPrimaryKeyAutoincrement());

        final SQLiteDatabase db = storm.database().open();

        try {

            final long id = db.insert(
                    metadata.tableName(),
                    null,
                    cv
            );

            if (id > 0L) {
                storm.notifyChange(table);
            }

            return id;

        } finally {
            storm.database().close();
        }
    }
}
