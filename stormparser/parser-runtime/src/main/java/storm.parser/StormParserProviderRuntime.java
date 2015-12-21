package storm.parser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Serialize;
import storm.annotations.Table;
import storm.reflect.TypeResolverLight;
import storm.serializer.StormSerializer;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
class StormParserProviderRuntime implements StormParserProvider {

    @Override
    public <T> StormParser<T> provideParser(
            Class<T> cl,
            StormInstanceCreator<T> instanceCreator,
            StormSerializerProvider serializerProvider
    ) throws StormParserException {
        return new StormParserRuntime<>(
                cl,
                getTableName(cl),
                buildColumns(cl),
                instanceCreator,
                serializerProvider
        );
    }

    static String getTableName(Class<?> cl) {
        final Table table = cl.getAnnotation(Table.class);
        final String name;
        if (table == null) {
            name = null;
        } else {
            final String tableValue = table.value();
            if (tableValue == null
                    || tableValue.length() == 0) {
                name = cl.getSimpleName();
            } else {
                name = tableValue;
            }
        }
        return name;
    }

    static List<StormParserColumn> buildColumns(Class<?> cl) throws StormParserException {

        final Field[] fields = cl.getDeclaredFields();
        if (fields == null
                || fields.length == 0) {
            throw StormParserException.newInstance("Class : `%s` has no fields", cl.getName());
        }

        final List<StormParserColumn> columns = new ArrayList<>();

        StormParserColumn column;

        for (Field field: fields) {

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            column = buildColumn(field);
            if (column != null) {
                columns.add(column);
            }
        }

        if (columns.size() == 0) {
            throw StormParserException.newInstance(
                    "Class `%s` has no suitable fields. Either marked as `transient` or has no `@Column` annotations",
                    cl.getName()
            );
        }

        return Collections.unmodifiableList(columns);
    }

    static StormParserColumn buildColumn(Field field) throws StormParserException {

        // is primary key
        // storm type
        // serializer
        // column name
        // field

        final Column column = field.getAnnotation(Column.class);

        if (Modifier.isTransient(field.getModifiers())
                || column == null) {
            return null;
        }

        final boolean isPrimaryKey;
        {
            isPrimaryKey = field.getAnnotation(PrimaryKey.class) != null;
        }

        final StormType type = parseType(field);

        final Class<?> serializerType;
        {
            final Serialize serialize = field.getAnnotation(Serialize.class);
            if (serialize == null) {
                serializerType = null;
            } else {
                serializerType = serialize.value();
            }
        }

        final String columnName;
        {
            final String columnValue = column.value();
            if (columnValue == null || columnValue.length() == 0) {
                columnName = field.getName();
            } else {
                columnName = columnValue;
            }
        }

        return new StormParserColumn()
                .setIsPrimaryKey(isPrimaryKey)
                .setStormType(type)
                .setName(columnName)
                .setSerializerType(serializerType)
                .setField(field);
    }

    static StormType parseType(Field field) throws StormParserException {

        final StormType type;

        final Serialize serialize = field.getAnnotation(Serialize.class);
        if (serialize == null) {
            type = StormType.forValue(field.getType());
        } else {

            final Class<?>[] typeArgs = TypeResolverLight.resolveRawArguments(StormSerializer.class, serialize.value());
            type = StormType.forValue(typeArgs[1]);
        }

        if (type == null) {
            throw StormParserException.newInstance(
                    "Type of the field `%s` in class `%s` could not be parsed. Type: %s",
                    field.getName(), field.getDeclaringClass(), field.getType().getName()
            );
        }

        return type;
    }
}
