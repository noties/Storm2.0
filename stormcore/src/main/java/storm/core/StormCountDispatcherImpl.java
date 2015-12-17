package storm.core;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
class StormCountDispatcherImpl implements StormCountDispatcher {

    private static final String QUERY_PATTERN = "SELECT COUNT(1) FROM %1$s";
    private static final String QUERY_WHERE_PATTERN = QUERY_PATTERN + " WHERE %2$s";

    @Override
    public <T extends StormObject> int execute(Storm storm, Class<T> table, Selection selection) {

        final String statement;
        final String[] args;

        if (selection == null) {
            statement = String.format(QUERY_PATTERN, null);
            args = null;
        } else {
            statement = String.format(QUERY_WHERE_PATTERN, null, selection.getStatement());
            args = selection.getArguments();
        }

        final SQLiteDatabase db = storm.database().open();
        try {

            final Cursor cursor = db.rawQuery(statement, args);
            if (cursor == null) {
                return 0;
            }

            try {

                if (!cursor.moveToFirst()) {
                    return 0;
                }

                return cursor.getInt(0);

            } finally {
                cursor.close();
            }

        } finally {
            storm.database().close();
        }
    }
}
