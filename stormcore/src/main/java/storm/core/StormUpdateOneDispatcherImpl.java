package storm.core;

import android.database.sqlite.SQLiteDatabase;

import storm.parser.StormParser;
import storm.parser.converter.StormConverter;
import storm.parser.metadata.PrimaryKeySelection;
import storm.parser.metadata.StormMetadata;
import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
class StormUpdateOneDispatcherImpl implements StormUpdateOneDispatcher {

    @Override
    public <T extends StormObject> int update(Storm storm, T value) {

        if (value == null) {
            throw new NullPointerException("Cannot update NULL value");
        }

        //noinspection unchecked
        final Class<T> table = (Class<T>) value.getClass();
        final StormParser<T> parser = storm.parser(table);
        final StormMetadata<T> metadata = parser.metadata();
        final StormConverter<T> converter = storm.converter(table, parser);

        final PrimaryKeySelection primaryKeySelection = metadata.primaryKeySelection(value);
        final Selection selection = new Selection().equals(
                primaryKeySelection.getPrimaryKeyName(),
                primaryKeySelection.getPrimaryKeyValue()
        );

        final SQLiteDatabase db = storm.database().open();

        try {

            final int updated = db.update(
                    metadata.tableName(),
                    converter.toContentValues(value, false),
                    selection.getStatement(),
                    selection.getArguments()
            );

            if (updated > 0) {
                storm.notifyChange(table);
            }

            return updated;

        } finally {
            storm.database().close();
        }
    }
}
