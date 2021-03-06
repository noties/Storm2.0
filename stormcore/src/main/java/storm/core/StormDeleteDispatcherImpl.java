package storm.core;

import android.database.sqlite.SQLiteDatabase;

import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
class StormDeleteDispatcherImpl implements StormDeleteDispatcher {

    @Override
    public <T extends StormObject> int execute(Storm storm, Class<T> table, Selection selection) {

        final SQLiteDatabase db = storm.database().open();

        try {

            final String where  = selection != null ? selection.getStatement() : null;
            final String[] args = selection != null ? selection.getArguments() : null;

            final int deleted = db.delete(
                    storm.tableName(table),
                    where,
                    args
            );

            if (deleted > 0) {
                storm.notifyChange(table);
            }

            return deleted;

        } finally {
            storm.database().close();
        }
    }
}
