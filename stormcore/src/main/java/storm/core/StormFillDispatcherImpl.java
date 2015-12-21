package storm.core;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import storm.parser.StormParser;
import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
class StormFillDispatcherImpl implements StormFillDispatcher {

    @Override
    public <T extends StormObject> int fill(Storm storm, Selection selection, T value) {

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

        final ContentValues cv = parser.toContentValues(value, false);

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
}
