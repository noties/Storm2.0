package storm.parser;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.serializer.StormSerializer;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
class StormParserRuntime<T> implements StormParser<T> {

    private final Class<T> mTable;
    private final String mTableName;
    private final List<StormParserColumn> mColumns;
    private final StormInstanceCreator<T> mInstanceCreator;
    private final StormSerializerProvider mSerializerProvider;

    private StormTableMetadata<T> mMetadata;

    StormParserRuntime(
            Class<T> table,
            String tableName,
            List<StormParserColumn> columns,
            StormInstanceCreator<T> instanceCreator,
            StormSerializerProvider serializerProvider
    ) {
        this.mTable = table;
        this.mTableName = tableName;
        this.mColumns = columns;
        this.mInstanceCreator = instanceCreator;
        this.mSerializerProvider = serializerProvider;
    }

    @Override
    public T fromCursor(Cursor cursor) {

        final Map<String, Integer> cursorIndexes = buildCursorIndexes(cursor);

        if (cursorIndexes == null) {
            return null;
        }

        return parse(cursor, cursorIndexes);
    }

    @Override
    public List<T> fromCursorList(Cursor cursor) {

        final Map<String, Integer> cursorIndexes = buildCursorIndexes(cursor);

        if (cursorIndexes == null) {
            return Collections.emptyList();
        }

        final List<T> list = new ArrayList<>(cursor.getCount());

        while (!cursor.isAfterLast()) {
            list.add(parse(cursor, cursorIndexes));
            cursor.moveToNext();
        }

        return list;
    }

    private T parse(Cursor cursor, Map<String, Integer> cursorIndexes) {

        final T instance = mInstanceCreator.create();

        Integer cacheValueIndex;
        int cursorIndex;
        int cursorType;
        StormType type;
        Field field;
        Class<?> serializerClass;

        for (StormParserColumn column: mColumns) {

            cacheValueIndex = cursorIndexes.get(column.getName());
            if (cacheValueIndex == null || cacheValueIndex < 0) {
                continue;
            }

            cursorIndex = cacheValueIndex;
            cursorType  = cursor.getType(cursorIndex);
            type = column.getType();
            field = column.getField();
            serializerClass = column.getSerializerType();

            final Object rawValue;
            switch (cursorType) {

                case Cursor.FIELD_TYPE_INTEGER:
                    if (type == StormType.INT) {
                        rawValue = cursor.getInt(cursorIndex);
                    } else {
                        rawValue = cursor.getLong(cursorIndex);
                    }
                    break;

                case Cursor.FIELD_TYPE_FLOAT:
                    if (type == StormType.FLOAT) {
                        rawValue = cursor.getFloat(cursorIndex);
                    } else {
                        rawValue = cursor.getDouble(cursorIndex);
                    }
                    break;

                case Cursor.FIELD_TYPE_STRING:
                    rawValue = cursor.getString(cursorIndex);
                    break;

                case Cursor.FIELD_TYPE_BLOB:
                    rawValue = cursor.getBlob(cursorIndex);
                    break;

//                case Cursor.FIELD_TYPE_NULL:
                default:
                    rawValue = null;
            }

            if (rawValue == null) {
                continue;
            }

            final Object value;
            if (serializerClass == null) {
                value = rawValue;
            } else {
                final StormSerializer serializer = mSerializerProvider.provide(serializerClass);
                //noinspection unchecked
                value = serializer.deserialize(rawValue);
            }

            try {
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(String.format(
                        "Could not access field `%s` in class `%s`",
                        field.getName(),
                        field.getDeclaringClass()
                ), e);
            }
        }

        return instance;
    }

    @Override
    public ContentValues toContentValues(T instance, boolean putPrimaryKey) {

        final int outValuesLength = putPrimaryKey ? mColumns.size() : mColumns.size() - 1;
        final ContentValues cv = new ContentValues(outValuesLength);

        StormType type;
        Field field;
        String name;
        Class<?> serializerClass;
        Object rawValue;

        for (StormParserColumn column: mColumns) {

            if (!putPrimaryKey && column.isPrimaryKey()) {
                continue;
            }

            type = column.getType();
            field = column.getField();
            name = column.getName();
            serializerClass = column.getSerializerType();
            rawValue = null;

            try {
                rawValue = field.get(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        String.format("Could not access field `%s` in class `%s`", field.getName(), field.getDeclaringClass()),
                        e
                );
            }

            final Object value;
            if (rawValue == null) {
                value = null;
            } else if (serializerClass == null) {
                value = rawValue;
            } else {
                final StormSerializer serializer = mSerializerProvider.provide(serializerClass);
                //noinspection unchecked
                value = serializer.serialize(rawValue);
            }

            switch (type) {

                case INT:
                    cv.put(name, (Integer) value);
                    break;

                case LONG:
                    cv.put(name, (Long) value);
                    break;

                case FLOAT:
                    cv.put(name, (Float) value);
                    break;

                case DOUBLE:
                    cv.put(name, (Double) value);
                    break;

                case STRING:
                    cv.put(name, (String) value);
                    break;

                case BYTE_ARRAY:
                    cv.put(name, (byte[]) value);
                    break;

            }
        }

        return cv;
    }

    @Override
    public synchronized StormTableMetadata<T> getMetadata() {
        if (mMetadata == null) {
            mMetadata = buildMetadata();
        }
        return mMetadata;
    }

    private static Map<String, Integer> buildCursorIndexes(Cursor cursor) {
        final String[] names = cursor.getColumnNames();
        final int length = names != null ? names.length : 0;
        if (length == 0) {
            return null;
        }

        final Map<String, Integer> map = new HashMap<>(length);

        for (int i = 0; i < length; i++) {
            map.put(names[i], i);
        }

        return map;
    }

    private StormTableMetadata<T> buildMetadata() {

        final StormParserColumn primaryKey;
        {
            StormParserColumn out = null;
            for (StormParserColumn column: mColumns) {
                if (column.isPrimaryKey()) {
                    out = column;
                    break;
                }
            }
            primaryKey = out;
        }

        if (primaryKey == null) {
            throw new RuntimeException("Table `" + mTableName + "` has no primary key");
        }

        final boolean isAutoincrement;
        {
            final PrimaryKey key = primaryKey.getField().getAnnotation(PrimaryKey.class);
            isAutoincrement = key.autoincrement();
        }

        final Uri notificationUri;
        {
            final Table table = mTable.getAnnotation(Table.class);
            if (table == null) {
                notificationUri = null;
            } else {
                notificationUri = StormNotificationUriBuilder.getDefault(mTable, table.notificationUri());
            }
        }

        return new StormTableMetadataRuntime<>(
                mTableName,
                isAutoincrement,
                notificationUri,
                primaryKey.getName(),
                primaryKey.getField()
        );
    }
}
