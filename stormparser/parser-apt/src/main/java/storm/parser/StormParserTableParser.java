package storm.parser;

import java.util.ArrayList;
import java.util.List;

import storm.annotations.Column;
import storm.annotations.Default;
import storm.annotations.ForeignKey;
import storm.annotations.Index;
import storm.annotations.NewColumn;
import storm.annotations.NewTable;
import storm.annotations.PrimaryKey;
import storm.annotations.SQLiteNotNull;
import storm.annotations.Serialize;
import storm.annotations.Table;
import storm.annotations.Unique;
import storm.parser.scheme.StormSchemeForeignKey;
import storm.parser.scheme.StormSchemeIndex;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
class StormParserTableParser {

    <MAIN, ELEMENT, TYPE> StormParserTable<MAIN, ELEMENT, TYPE> parseTable(
            StormParserHelper<MAIN, ELEMENT, TYPE> helper,
            MAIN main
    ) throws StormParserException {

        final String tableName;
        final boolean isRecreateOnUpgrade;
        final String customNotificationUri;
        {
            final Table table = helper.getMainAnnotation(main, Table.class);
            if (table == null) {
                tableName = null;
                isRecreateOnUpgrade = false;
                customNotificationUri = null;
            } else {
                final String tableValue = table.value();
                if (tableValue == null || tableValue.length() == 0) {
                    tableName = helper.getMainSimpleName(main);
                } else {
                    tableName = tableValue;
                }
                isRecreateOnUpgrade = table.recreateOnUpgrade();
                customNotificationUri = table.notificationUri();
            }
        }

        final int versionWhenAdded;
        {
            final NewTable newTable = helper.getMainAnnotation(main, NewTable.class);
            if (newTable == null) {
                versionWhenAdded = 0;
            } else {
                versionWhenAdded = newTable.value();
            }
        }

        return new StormParserTable<MAIN, ELEMENT, TYPE>(
                main,
                tableName,
                parseColumns(helper, main),
                versionWhenAdded,
                isRecreateOnUpgrade,
                customNotificationUri
        );
    }

    <MAIN, ELEMENT, TYPE> List<StormParserColumn<ELEMENT, TYPE>> parseColumns(
            StormParserHelper<MAIN, ELEMENT, TYPE> helper,
            MAIN main
    ) throws StormParserException {

        final List<ELEMENT> elements = helper.getElements(main);
        if (elements == null
                || elements.size() == 0) {
            throw StormParserException.newInstance("Class `%s` has no elements", main);
        }

        final List<StormParserColumn<ELEMENT, TYPE>> columns = new ArrayList<>();
        StormParserColumn<ELEMENT, TYPE> column;

        for (ELEMENT element: elements) {
            column = parseColumn(helper, main, element);
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

    <MAIN, ELEMENT, TYPE> StormParserColumn<ELEMENT, TYPE> parseColumn(
            StormParserHelper<MAIN, ELEMENT, TYPE> helper,
            MAIN main,
            ELEMENT element
    ) throws StormParserException {

        if (!helper.shouldParseElement(element)) {
            return null;
        }

        final Column column = helper.getElementAnnotation(element, Column.class);
        if (column == null) {
            throw StormParserException.newInstance("Field `%s` in class `%s` should be included " +
                            "in table scheme, but is not annotated with `@Column`. " +
                            "If field should not be included add `transient` modifier. " +
                            "Else annotate with `@Column` (even if field is already annotated with other Storm annotations)",
                    helper.getElementSimpleName(element), main);
        }

        final boolean isPrimaryKey;
        final boolean isAutoincrement;
        {
            final PrimaryKey primaryKey = helper.getElementAnnotation(element, PrimaryKey.class);
            isPrimaryKey = primaryKey != null;
            isAutoincrement = isPrimaryKey && primaryKey.autoincrement();
        }

        final String columnName;
        {
            final String columnValue = column.value();
            if (columnValue == null || columnValue.length() == 0) {
                columnName = helper.getElementSimpleName(element);
            } else {
                columnName = columnValue;
            }
        }

        final TYPE serializerType;
        final boolean isSerializerGeneric;
        {
            final Serialize serialize = helper.getElementAnnotation(element, Serialize.class);
            if (serialize == null) {
                serializerType = null;
                isSerializerGeneric = false;
            } else {
                serializerType = helper.getSerializeValue(serialize);
                isSerializerGeneric = helper.isSerializerGeneric(serialize);
            }
        }

        final StormType stormType = parseType(helper, main, element);

        final boolean isUnique;
        {
            final Unique unique = helper.getElementAnnotation(element, Unique.class);
            isUnique = unique != null;
        }

        final String defValue;
        {
            final Default def = helper.getElementAnnotation(element, Default.class);
            if (def == null) {
                defValue = null;
            } else {
                defValue = def.value();
            }
        }

        final boolean isNonNull;
        {
            final SQLiteNotNull sqLiteNotNull = helper.getElementAnnotation(element, SQLiteNotNull.class);
            isNonNull = sqLiteNotNull != null;
        }

        final StormSchemeIndex index;
        {
            final Index indexAnnotation = helper.getElementAnnotation(element, Index.class);
            if (indexAnnotation == null) {
                index = null;
            } else {
                index = new StormSchemeIndex(indexAnnotation.value(), indexAnnotation.sorting().name());
            }
        }

        final StormSchemeForeignKey foreignKey;
        {
            final ForeignKey foreignKeyAnnotation = helper.getElementAnnotation(element, ForeignKey.class);
            if (foreignKeyAnnotation == null) {
                foreignKey = null;
            } else {
                final ForeignKey.ForeignKeyAction onUpdate = foreignKeyAnnotation.onUpdate();
                final ForeignKey.ForeignKeyAction onDelete = foreignKeyAnnotation.onDelete();

                final StormSchemeForeignKey key = new StormSchemeForeignKey(
                        foreignKeyAnnotation.parentTable(),
                        foreignKeyAnnotation.parentColumnName()
                );

                if (onUpdate != ForeignKey.ForeignKeyAction.NO_ACTION) {
                    key.setOnUpdateAction(onUpdate.name());
                }

                if (onDelete != ForeignKey.ForeignKeyAction.NO_ACTION) {
                    key.setOnDeleteAction(onDelete.name());
                }
                foreignKey = key;
            }
        }

        final int versionWhenAdded;
        {
            final NewColumn newColumn = helper.getElementAnnotation(element, NewColumn.class);
            if (newColumn == null) {
                versionWhenAdded = 0;
            } else {
                versionWhenAdded = newColumn.value();
            }
        }

        return new StormParserColumn<ELEMENT, TYPE>()
                .setIsPrimaryKey(isPrimaryKey)
                .setIsAutoIncrement(isAutoincrement)
                .setName(columnName)
                .setElement(element)
                .setSerializerType(serializerType)
                .setStormType(stormType)
                .setIsUnique(isUnique)
                .setDefValue(defValue)
                .setIsNonNull(isNonNull)
                .setIndex(index)
                .setForeignKey(foreignKey)
                .setVersionWhenAdded(versionWhenAdded)
                .setIsSerializerGeneric(isSerializerGeneric);
    }

    <MAIN, ELEMENT, TYPE> StormType parseType(
            StormParserHelper<MAIN, ELEMENT, TYPE> helper,
            MAIN main,
            ELEMENT element
    ) throws StormParserException {

        final Serialize serialize = helper.getElementAnnotation(element, Serialize.class);

        final StormType stormType;

        if (serialize == null) {
            stormType = helper.getType(helper.getElementType(element));
        } else {
            TYPE serializerType;
            try {
                serializerType = helper.getSerializeType(serialize);
            } catch (StormParserHelper.SerializerNotOfTypeException e) {
                throw StormParserException.newInstance("Field `%s` in class `%s` has registered Serializer, " +
                                "but it's not of type `StormSerializer`",
                        helper.getElementSimpleName(element), main);
            } catch (StormParserHelper.SerializerTypeNotSqliteType serializerTypeNotSqliteType) {
                throw StormParserException.newInstance("Field `%s` in class `%s` has registered serializer, " +
                                "but it's not a valid one. Check type parameters of a supplied serializer",
                        helper.getElementSimpleName(element), main);
            } catch (StormParserHelper.SerializerWrongTypeArgumentsException e) {
                throw StormParserException.newInstance("Field `%s` in class `%s` has registered serializer " +
                                "with OUT type not supported by SQLite",
                        helper.getElementSimpleName(element), main);
            }
            stormType = helper.getType(serializerType);
        }

        if (stormType == null) {
            throw StormParserException.newInstance("Could not parse field's (%s) type in class: %s", element, main);
        }

        return stormType;
    }
}
