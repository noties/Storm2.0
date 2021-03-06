package storm.cursormock;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import storm.annotations.Column;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public class StormCursorMock implements Cursor {

    public static class Row {

        final Object[] values;

        public Row(Object... values) {
            this.values = values;
        }
    }

    public static StormCursorMock newInstance(Class<?> cl, Row... rows) {

        final Field[] fields = cl.getDeclaredFields();
        if (fields == null
                || fields.length == 0) {
            throw StormCursorMockException.newInstance("Class `%s` has no fields", cl.getName());
        }

        final List<String> names = new ArrayList<>();

        for (Field field: fields) {

            final String name;
            {
                final Column column = field.getAnnotation(Column.class);
                if (column == null) {
                    continue;
                }
                final String columnValue = column.value();
                if (columnValue == null || columnValue.length() == 0) {
                    name = field.getName();
                } else {
                    name = columnValue;
                }
            }
            names.add(name);
        }

        if (names.size() == 0) {
            throw StormCursorMockException.newInstance("Class `%s` has no fields annotated with @Column", cl.getName());
        }

        final String[] outNames = new String[names.size()];
        names.toArray(outNames);

        return new StormCursorMock(outNames, buildRows(rows));
    }

    private static Object[][] buildRows(Row[] rows) {

        if (rows == null
                || rows.length == 0) {
            return new Object[0][0];
        }

        final int rowsCount = rows.length;

        final Object[][] values = new Object[rowsCount][];

        for (int i = 0; i < rowsCount; i++) {
            values[i] = rows[i].values;
        }

        return values;
    }

    private final String[] mNames;
    private final Object[][] mValues;
    private final int mRows;

    private int mPosition = -1;
    private boolean mIsClosed;

    public StormCursorMock(String[] names, Object[][] values) {
        this.mNames = names;
        this.mValues = values;
        this.mRows = values.length;
    }

    @Override
    public int getCount() {
        return mRows;
    }

    @Override
    public int getPosition() {
        return mPosition;
    }

    @Override
    public boolean move(int offset) {
        mPosition += offset;
        return checkPosition();
    }

    @Override
    public boolean moveToPosition(int position) {
        mPosition = position;
        return checkPosition();
    }

    @Override
    public boolean moveToFirst() {
        mPosition = 0;
        return checkPosition();
    }

    @Override
    public boolean moveToLast() {
        mPosition = mRows -1;
        return checkPosition();
    }

    @Override
    public boolean moveToNext() {
        mPosition++;
        return checkPosition();
    }

    @Override
    public boolean moveToPrevious() {
        mPosition--;
        return checkPosition();
    }

    @Override
    public boolean isFirst() {
        return mPosition == 0;
    }

    @Override
    public boolean isLast() {
        return mPosition == mRows - 1;
    }

    @Override
    public boolean isBeforeFirst() {
        return mPosition == -1;
    }

    @Override
    public boolean isAfterLast() {
        return mPosition >= mRows;
    }

    @Override
    public int getColumnIndex(String columnName) {
        for (int i = 0, length = mNames.length; i < length; i++) {
            if (columnName.equals(mNames[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        return getColumnIndex(columnName);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return mNames[columnIndex];
    }

    @Override
    public String[] getColumnNames() {
        return mNames;
    }

    @Override
    public int getColumnCount() {
        return mNames.length;
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        return (byte[]) mValues[mPosition][columnIndex];
    }

    @Override
    public String getString(int columnIndex) {
        return (String) mValues[mPosition][columnIndex];
    }

    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
        throw StormCursorMockException.newInstance("Not implemented");
    }

    @Override
    public short getShort(int columnIndex) {
        throw StormCursorMockException.newInstance("short type is not supported");
    }

    @Override
    public int getInt(int columnIndex) {
        final Object value = mValues[mPosition][columnIndex];
        if (value == null) {
            return 0;
        }
        return (Integer) value;
    }

    @Override
    public long getLong(int columnIndex) {
        final Object value = mValues[mPosition][columnIndex];
        if (value == null) {
            return 0L;
        }
        return (Long) value;
    }

    @Override
    public float getFloat(int columnIndex) {
        final Object value = mValues[mPosition][columnIndex];
        if (value == null) {
            return .0F;
        }
        return (Float) value;
    }

    @Override
    public double getDouble(int columnIndex) {
        final Object value = mValues[mPosition][columnIndex];
        if (value == null) {
            return .0D;
        }
        return (Double) value;
    }

    @Override
    public int getType(int columnIndex) {

        final Object value = mValues[mPosition][columnIndex];
        if (value == null) {
            return FIELD_TYPE_NULL;
        }

        final StormType type = StormType.forValue(value.getClass());

        if (type == null) {
            throw StormCursorMockException.newInstance("Could not parse type of a class: `%s`", value.getClass().getName());
        }

        switch (type) {

            case INT:
            case LONG:
                return FIELD_TYPE_INTEGER;

            case FLOAT:
            case DOUBLE:
                return FIELD_TYPE_FLOAT;

            case STRING:
                return FIELD_TYPE_STRING;

            case BYTE_ARRAY:
                return FIELD_TYPE_BLOB;

        }

        throw StormCursorMockException.newInstance("Could not parse type: %s", type);
    }

    @Override
    public boolean isNull(int columnIndex) {
        return mValues[mPosition][columnIndex] == null;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public boolean requery() {
        return false;
    }

    @Override
    public void close() {
        mIsClosed = true;
    }

    @Override
    public boolean isClosed() {
        return mIsClosed;
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {
        throw StormCursorMockException.newInstance("Not implemented");
    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {
        throw StormCursorMockException.newInstance("Not implemented");
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        throw StormCursorMockException.newInstance("Not implemented");
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        throw StormCursorMockException.newInstance("Not implemented");
    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {
        throw StormCursorMockException.newInstance("Not implemented");
    }

    @Override
    public Uri getNotificationUri() {
        throw StormCursorMockException.newInstance("Not implemented");
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        throw StormCursorMockException.newInstance("Not implemented");
    }

    @Override
    public void setExtras(Bundle extras) {
        throw StormCursorMockException.newInstance("Not implemented");
    }

    @Override
    public Bundle getExtras() {
        throw StormCursorMockException.newInstance("Not implemented");
    }

    @Override
    public Bundle respond(Bundle extras) {
        throw StormCursorMockException.newInstance("Not implemented");
    }

    private boolean checkPosition() {
        return mPosition > -1 && mPosition < mRows;
    }
}

