package storm.core;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.List;

import storm.parser.converter.StormConverter;
import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
class StormFillDispatcherImpl implements StormFillDispatcher {

    @Override
    public <T extends StormObject> int fill(Storm storm, Selection selection, List<String> columns, boolean isInclude, T value) {

        if (value == null) {
            throw new NullPointerException("Cannot fill with NULL value");
        }

        final String where;
        final String[] args;

        if (selection == null) {
            where = null;
            args = null;
        } else {
            where = selection.getStatement();
            args = selection.getArguments();
        }

        //noinspection unchecked
        final Class<T> table = (Class<T>) value.getClass();
        final StormConverter<T> converter = storm.converter(table);

        // default behavior would be
        // if there is no columns rules - put all except primary key
        // else put all & filter based on columns rules

        final ContentValues cv;
        if (columns == null
                || columns.size() == 0) {
            cv = converter.toContentValues(value, false);
        } else {
            cv = filterValues(converter.toContentValues(value, true), columns, isInclude);
        }

        final SQLiteDatabase db = storm.database().open();
        final StormTransactionController transactionController = new StormTransactionController(db);

        try {

            transactionController.beginTransaction();

            try {

                final int updated = db.update(
                        storm.metadata(table).tableName(),
                        cv,
                        where,
                        args
                );

                transactionController.setTransactionSuccessful();

                if (updated > 0) {
                    storm.notifyChange(table);
                }

                return updated;

            } catch (SQLiteException e) {
                throw StormException.newInstance(
                        e,
                        "Exception during `fill` operation for a table: `%s`, value: `%s`, contentValues: `%s`",
                        table.getName(), value, cv
                );
            } finally {
                transactionController.endTransaction();
            }

        } finally {
            storm.database().close();
        }
    }

    private ContentValues filterValues(ContentValues cv, List<String> columns, boolean isInclude) {

        final ContentValues out;

        // exclude columns from list
        if (!isInclude) {
            for (String column: columns) {
                cv.remove(column);
            }
            out = cv;
        } else { // include columns only from list
            out = new ContentValues(cv);
            for (String key: cv.keySet()) {
                if (!columns.contains(key)) {
                    out.remove(key);
                }
            }
        }

        return out;
    }
}
