package storm.core;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.List;
import java.util.Set;

import storm.parser.StormParser;
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
        final StormParser<T> parser = storm.parser(table);

        // default behavior would be
        // if there is no columns rules - put all except primary key
        // else put all & filter based on columns rules

        final ContentValues cv;
        if (columns == null
                || columns.size() == 0) {
            cv = parser.toContentValues(value, false);
        } else {
            cv = filterValues(parser.toContentValues(value, true), columns, isInclude);
        }

        final SQLiteDatabase db = storm.database().open();

        try {

            final boolean hasTransaction = db.inTransaction();

            if (!hasTransaction) {
                db.beginTransaction();
            }

            try {

                final int updated = db.update(
                        parser.getMetadata().getTableName(),
                        cv,
                        where,
                        args
                );

                if (!hasTransaction) {
                    db.setTransactionSuccessful();
                }

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
                if (!hasTransaction) {
                    db.endTransaction();
                }
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
