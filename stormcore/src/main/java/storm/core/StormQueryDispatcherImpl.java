package storm.core;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collections;
import java.util.List;

import storm.iterator.CursorIterator;
import storm.iterator.CursorIteratorBaseImpl;
import storm.iterator.CursorIteratorCached;
import storm.iterator.CursorIteratorCachedImpl;
import storm.parser.converter.StormConverter;
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
                final StormConverter<T> converter = storm.converter(table);
                return converter.fromCursor(cursor);
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

            final StormConverter<T> converter = storm.converter(table);

            return converter.fromCursorList(cursor);

        } finally {
            cursor.close();
        }
    }

    @Override
    public <T extends StormObject> CursorIterator<T> asIterator(Storm storm, Class<T> table, Query query) {
        final Cursor cursor = asCursor(storm, query);
        return new CursorIteratorBaseImpl<>(
                cursor,
                new CursorIteratorParserBridge<>(storm.converter(table))
        );
    }

    @Override
    public <T extends StormObject> CursorIteratorCached<T> asCachedIterator(Storm storm, Class<T> table, Query query, int cacheSize) {
        final Cursor cursor = asCursor(storm, query);
        return new CursorIteratorCachedImpl<>(
                cursor,
                new CursorIteratorParserBridge<>(storm.converter(table)),
                cacheSize
        );
    }

}
