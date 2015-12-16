package storm.core;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import storm.iterator.CursorIterator;
import storm.iterator.CursorIteratorBaseImpl;
import storm.iterator.CursorIteratorCached;
import storm.iterator.CursorIteratorCachedImpl;
import storm.parser.StormParser;
import storm.query.Query;

/**
 * Created by Dimitry Ivanov on 16.12.2015.
 */
class StormQueryDispatcherImpl implements StormQueryDispatcher {

    @Override
    public Cursor asCursor(Storm storm, Query query) {
        final SQLiteDatabase database = storm.database().open();
        final Cursor cursor = database.rawQuery(query.getStatement(), query.getArguments());
        if (cursor != null) {
            cursor.registerDataSetObserver(new CloseDatabaseReferenceObserver(storm));
        }
        return cursor;
    }

    @Override
    public <T extends StormObject> T asOne(Storm storm, Class<T> table, Query query) {
        final Cursor cursor = asCursor(storm, query);
        if (cursor == null) {
            return null;
        }

        try {

            if (cursor.moveToFirst()) {
                final StormParser<T> parser = storm.parser(table);
                return parser.fromCursor(cursor);
            }

        } finally {
            cursor.close();
        }

        return null;
    }

    @Override
    public <T extends StormObject> List<T> asList(Storm storm, Class<T> table, Query query) {
        final Cursor cursor = asCursor(storm, query);
        if (cursor == null) {
            return null;
        }

        try {

            if (!cursor.moveToFirst()) {
                return Collections.emptyList();
            }

            final List<T> list = new ArrayList<>(cursor.getCount());
            final StormParser<T> parser = storm.parser(table);

            while (!cursor.isAfterLast()) {
                list.add(parser.fromCursor(cursor));
                cursor.moveToNext();
            }

            return list;

        } finally {
            cursor.close();
        }
    }

    @Override
    public <T extends StormObject> CursorIterator<T> asIterator(Storm storm, Class<T> table, Query query) {
        final Cursor cursor = asCursor(storm, query);
        return new CursorIteratorBaseImpl<>(
                cursor,
                new CursorIteratorParserBridge<>(storm.parser(table))
        );
    }

    @Override
    public <T extends StormObject> CursorIteratorCached<T> asCachedIterator(Storm storm, Class<T> table, Query query, int cacheSize) {
        final Cursor cursor = asCursor(storm, query);
        return new CursorIteratorCachedImpl<>(
                cursor,
                new CursorIteratorParserBridge<>(storm.parser(table)),
                cacheSize
        );
    }

    private static class CloseDatabaseReferenceObserver extends DataSetObserver {

        final Storm mStorm;

        CloseDatabaseReferenceObserver(Storm storm) {
            mStorm = storm;
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mStorm.database().close();
        }
    }
}
