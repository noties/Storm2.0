package storm.parser;

import java.util.ArrayList;
import java.util.List;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Serialize;
import storm.annotations.Table;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
class StormParserTableParser {

    <MAIN, ELEMENT, TYPE> StormParserTable<MAIN, ELEMENT, TYPE> parseTable(
            StormParserHelper<MAIN, ELEMENT, TYPE> type,
            MAIN main
    ) throws StormParserException {

        final String tableName;
        {
            final Table table = type.getMainAnnotation(main, Table.class);
            if (table == null) {
                tableName = null;
            } else {
                final String tableValue = table.value();
                if (tableValue == null || tableValue.length() == 0) {
                    tableName = type.getMainSimpleName(main);
                } else {
                    tableName = tableValue;
                }
            }
        }

        return new StormParserTable<MAIN, ELEMENT, TYPE>(main, tableName, parseColumns(type, main));
    }

    <MAIN, ELEMENT, TYPE> List<StormParserColumn<ELEMENT, TYPE>> parseColumns(
            StormParserHelper<MAIN, ELEMENT, TYPE> type,
            MAIN main
    ) throws StormParserException {

        final List<ELEMENT> elements = type.getElements(main);
        if (elements == null
                || elements.size() == 0) {
            throw StormParserException.newInstance("Class `%s` has no elements", main);
        }

        final List<StormParserColumn<ELEMENT, TYPE>> columns = new ArrayList<>();
        StormParserColumn<ELEMENT, TYPE> column;

        for (ELEMENT element: elements) {
            column = parserColumn(type, main, element);
            if (column != null) {
                columns.add(column);
            }
        }

        if (columns.size() == 0) {
            throw StormParserException.newInstance("Class `%s` has no suitable fields. " +
                    "All either marked as transient or missing `@Column` annotation", main);
        }

        return columns;
    }

    <MAIN, ELEMENT, TYPE> StormParserColumn<ELEMENT, TYPE> parserColumn(
            StormParserHelper<MAIN, ELEMENT, TYPE> type,
            MAIN main,
            ELEMENT element
    ) throws StormParserException {

        // is primary key
        // storm type
        // serializer
        // column name
        // field, todo....

        final Column column = type.getElementAnnotation(element, Column.class);
        if (column == null) {
            return null;
        }

        if (!type.shouldParseElement(element)) {
            return null;
        }

        final boolean isPrimaryKey;
        final boolean isAutoincrement;
        {
            final PrimaryKey primaryKey = type.getElementAnnotation(element, PrimaryKey.class);
            isPrimaryKey = primaryKey != null;
            isAutoincrement = isPrimaryKey && primaryKey.autoincrement();
        }

        final String columnName;
        {
            final String columnValue = column.value();
            if (columnValue == null || columnValue.length() == 0) {
                columnName = type.getElementSimpleName(element);
            } else {
                columnName = columnValue;
            }
        }

        final TYPE serializerType;
        {
            final Serialize serialize = type.getElementAnnotation(element, Serialize.class);
            if (serialize == null) {
                serializerType = null;
            } else {
                serializerType = type.getSerializeValue(serialize);
            }
        }

        final StormType stormType = parseType(type, main, element);

        return new StormParserColumn<ELEMENT, TYPE>()
                .setIsPrimaryKey(isPrimaryKey)
                .setIsAutoIncrement(isAutoincrement)
                .setName(columnName)
                .setElement(element)
                .setSerializerType(serializerType)
                .setStormType(stormType);
    }

    <MAIN, ELEMENT, TYPE> StormType parseType(
            StormParserHelper<MAIN, ELEMENT, TYPE> type,
            MAIN main,
            ELEMENT element
    ) throws StormParserException {

        final Serialize serialize = type.getElementAnnotation(element, Serialize.class);

        final StormType stormType;

        if (serialize == null) {
            stormType = type.getType(type.getElementType(element));
        } else {
            stormType = type.getType(type.getSerializeType(serialize));
        }

        if (stormType == null) {
            throw StormParserException.newInstance("Could not parse field's (%s) type in class: %s", element, main);
        }

        return stormType;
    }
}
