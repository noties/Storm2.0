package storm.core;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.Collection;

import storm.parser.PrimaryKeySelection;
import storm.parser.StormParser;
import storm.parser.StormTableMetadata;
import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
class StormUpdateManyDispatcherImpl implements StormUpdateManyDispatcher {

    @Override
    public <T extends StormObject> int update(Storm storm, Collection<T> values) {

        if (values == null) {
            throw new NullPointerException("Cannot value NULL values");
        }

        //noinspection unchecked
        final Class<T> table = (Class<T>) values.iterator().next().getClass();
        final StormParser<T> parser = storm.parser(table);
        final StormTableMetadata<T> metadata = parser.getMetadata();

        final String tableName = metadata.getTableName();

        final SQLiteDatabase db = storm.database().open();

        int updated = 0;

        try {

            final boolean hasTransactionAlready = db.inTransaction();

            if (!hasTransactionAlready) {
                db.beginTransaction();
            }

            try {

                PrimaryKeySelection primaryKeySelection;
                Selection selection;

                for (T value: values) {

                    primaryKeySelection = metadata.getPrimaryKeySelection(value);
                    selection = new Selection().equals(
                            primaryKeySelection.getPrimaryKeyName(),
                            primaryKeySelection.getPrimaryKeyValue()
                    );

                    updated += db.update(
                            tableName,
                            parser.toContentValues(value, false),
                            selection.getStatement(),
                            selection.getArguments()
                    );

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

        return updated;
    }
}
