package storm.core;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import storm.parser.StormParser;
import storm.parser.converter.StormConverter;
import storm.parser.metadata.PrimaryKeySelection;
import storm.parser.metadata.StormMetadata;
import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
class StormUpdateManyDispatcherImpl implements StormUpdateManyDispatcher {

    @Override
    public <T extends StormObject> int update(Storm storm, Collection<T> collection) {

        if (collection == null) {
            throw new NullPointerException("Cannot value NULL values");
        }

        //noinspection unchecked
        final Class<T> table = (Class<T>) collection.iterator().next().getClass();
        final StormParser<T> parser = storm.parser(table);
        final StormMetadata<T> metadata = storm.metadata(table, parser);
        final StormConverter<T> converter = storm.converter(table, parser);

        final List<T> values = new ArrayList<>(collection);

        final List<ContentValues> contentValues = converter.toContentValuesList(values, false);
        if (contentValues == null
                || contentValues.size() == 0) {
            return 0;
        }

        final String tableName = metadata.tableName();

        final SQLiteDatabase db = storm.database().open();
        final StormTransactionController transactionController = new StormTransactionController(db);

        int index = 0;
        int updated = 0;

        try {

            transactionController.beginTransaction();

            try {

                PrimaryKeySelection primaryKeySelection;
                Selection selection;
                T value;

                for (ContentValues cv: contentValues) {
                    value = values.get(index);
                    primaryKeySelection = metadata.primaryKeySelection(value);
                    selection = new Selection().equals(
                            primaryKeySelection.getPrimaryKeyName(),
                            primaryKeySelection.getPrimaryKeyValue()
                    );

                    updated += db.update(
                            tableName,
                            cv,
                            selection.getStatement(),
                            selection.getArguments()
                    );
                    index++;
                }

                transactionController.setTransactionSuccessful();

                if (updated > 0) {
                    storm.notifyChange(table);
                }

            } catch (SQLiteException e) {
                throw StormException.newInstance(
                        e,
                        "Exception during `updateMany` operation for a class: `%s`, values: `%s`",
                        table.getName(), values
                );
            } finally {
                transactionController.endTransaction();
            }


        } finally {
            storm.database().close();
        }

        return updated;
    }
}
