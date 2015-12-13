package storm.parser;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.SparseArray;

import java.util.List;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
class StormParserRuntime<T> implements StormParser<T> {

    // instance creators
    // serializers

    private final List<StormParserColumn> mColumns;

    private SparseArray<String> mCursorIndexes;

    StormParserRuntime(List<StormParserColumn> columns) {
        mColumns = columns;
    }

    @Override
    public T fromCursor(Cursor cursor) {

        if (mCursorIndexes == null) {
            mCursorIndexes = buildCursorIndexes(cursor);
        }

        if (mCursorIndexes == null) {
            return null;
        }

        // here we need reflect fields (name, field)

        return null;
    }

    @Override
    public ContentValues toContentValues(T value, boolean putPrimaryKey) {

        final int outValuesLength = putPrimaryKey ? mColumns.size() : mColumns.size() - 1;
        final ContentValues cv = new ContentValues(outValuesLength);

        ;

        return cv;
    }

    private static SparseArray<String> buildCursorIndexes(Cursor cursor) {
        final String[] names = cursor.getColumnNames();
        final int length = names != null ? names.length : 0;
        if (length == 0) {
            return null;
        }

        final SparseArray<String> array = new SparseArray<>(length);

        for (int i = 0; i < length; i++) {
            array.put(i, names[i]);
        }

        return array;
    }
}
