package storm.parser;

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
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class StormCursorMock implements Cursor {

    static class Row {

        final Object[] values;

        Row(Object... values) {
            this.values = values;
        }
    }

    static StormCursorMock newInstance(Class<?> cl, Row... rows) {

        final Field[] fields = cl.getDeclaredFields();

        final List<String> names = new ArrayList<>();

        for (Field field: fields) {

            final String name;
            {
                final Column column = field.getAnnotation(Column.class);
                final String columnValue = column.value();
                if (columnValue == null || columnValue.length() == 0) {
                    name = field.getName();
                } else {
                    name = columnValue;
                }
            }
            names.add(name);
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

    private int mPosition;
    private boolean mIsClosed;

    StormCursorMock(String[] names, Object[][] values) {
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
        return 0;
    }

    @Override
    public boolean move(int offset) {
        return false;
    }

    @Override
    public boolean moveToPosition(int position) {
        return false;
    }

    @Override
    public boolean moveToFirst() {
        if (mRows > 0) {
            mPosition = 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean moveToLast() {
        return false;
    }

    @Override
    public boolean moveToNext() {
        return (++mPosition < mRows);
    }

    @Override
    public boolean moveToPrevious() {
        return false;
    }

    @Override
    public boolean isFirst() {
        return false;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public boolean isBeforeFirst() {
        return false;
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

    }

    @Override
    public short getShort(int columnIndex) {
        return 0;
    }

    @Override
    public int getInt(int columnIndex) {
        return (Integer) mValues[mPosition][columnIndex];
    }

    @Override
    public long getLong(int columnIndex) {
        return (Long) mValues[mPosition][columnIndex];
    }

    @Override
    public float getFloat(int columnIndex) {
        return (Float) mValues[mPosition][columnIndex];
    }

    @Override
    public double getDouble(int columnIndex) {
        return (Double) mValues[mPosition][columnIndex];
    }

    @Override
    public int getType(int columnIndex) {

        final Object value = mValues[mPosition][columnIndex];
        if (value == null) {
            return FIELD_TYPE_NULL;
        }

        final StormType type = StormType.forValue(value.getClass());

        if (type == null) {
            throw new RuntimeException(new NullPointerException());
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

        throw new RuntimeException("Could not parse type: " + type);
    }

    @Override
    public boolean isNull(int columnIndex) {
        return false;
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

    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {

    }

    @Override
    public Uri getNotificationUri() {
        return null;
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    @Override
    public void setExtras(Bundle extras) {

    }

    @Override
    public Bundle getExtras() {
        return null;
    }

    @Override
    public Bundle respond(Bundle extras) {
        return null;
    }
}
