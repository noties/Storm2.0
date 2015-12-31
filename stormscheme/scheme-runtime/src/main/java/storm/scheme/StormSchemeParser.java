package storm.scheme;

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
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
class StormSchemeParser {

    <MAIN, ELEMENT, TYPE> StormSchemeTable parseTable(MAIN main, StormSchemeType<MAIN, ELEMENT, TYPE> type) throws StormSchemeException {

        if (!type.isMainValid(main)) {
            throw StormSchemeException.newInstance("Class `%s` must be annotated with @Table" +
                    " in order to build StormScheme", main);
        }

        if (!type.shouldGenerateScheme(main)) {
            return null;
        }

        final StormSchemeTable table = getTable(type, main);

        final List<ELEMENT> elements = type.getElements(main);
        if (elements == null
                || elements.size() == 0) {
            throw StormSchemeException.newInstance("Class `%s` has no fields", main);
        }

        final List<StormSchemeColumn> columns = new ArrayList<>();

        StormSchemeColumn column;
        boolean hasPrimaryKey = false;

        for (ELEMENT element: elements) {

            column = parseColumn(type, main, element);
            if (column == null) {
                continue;
            }

            columns.add(column);
            hasPrimaryKey |= column.isPrimaryKey();
        }

        if (columns.size() == 0) {
            throw StormSchemeException.newInstance("Class `%s` has no columns creation statements.", main);
        }

        if (!hasPrimaryKey) {
            throw StormSchemeException.newInstance("Class `%s` has no primary key." +
                    " Field must be annotated with @PrimaryKey", main);
        }

        return table.setColumns(columns);
    }

    <MAIN, ELEMENT, TYPE> String getTableName(StormSchemeType<MAIN, ELEMENT, TYPE> type, MAIN main) throws StormSchemeException {
        final Table table = type.getMainAnnotation(main, Table.class);
        final String tableValue = table.value();
        if (tableValue == null || tableValue.length() == 0) {
            return type.getMainSimpleName(main);
        } else {
            return tableValue;
        }
    }

    <MAIN, ELEMENT, TYPE> StormSchemeTable getTable(StormSchemeType<MAIN, ELEMENT, TYPE> type, MAIN main) throws StormSchemeException {

        final Table table = type.getMainAnnotation(main, Table.class);

        final String tableName;
        {
            final String tableValue = table.value();
            if (tableValue == null || tableValue.length() == 0) {
                tableName = type.getMainSimpleName(main);
            } else {
                tableName = tableValue;
            }
        }

        final boolean isRecreateOnUpgrade;
        {
            isRecreateOnUpgrade = table.recreateOnUpgrade();
        }

        final int versionWhenAdded;
        {
            final NewTable newTable = type.getMainAnnotation(main, NewTable.class);
            versionWhenAdded = newTable != null ? newTable.value() : 0;
        }

        return new StormSchemeTable(tableName, null, versionWhenAdded, isRecreateOnUpgrade);
    }

    private <MAIN, ELEMENT, TYPE> StormSchemeColumn parseColumn(
            StormSchemeType<MAIN, ELEMENT, TYPE> type,
            MAIN main,
            ELEMENT element
    ) throws StormSchemeException {

        if (!type.shouldParseElement(element)) {
            return null;
        }

        final String name;
        {
            final Column column = type.getElementAnnotation(element, Column.class);
            if (column == null) {
                throw StormSchemeException.newInstance("Field `%s` in class `%s` should be included " +
                                "in table scheme, but is not annotated with `@Column`. " +
                                "If field should not be included add `transient` modifier. " +
                                "Else annotate with `@Column` (even if field is already annotated with `@PrimaryKey`)",
                        type.getElementSimpleName(element), main
                );
            }

            final String columnValue = column.value();
            if (columnValue == null || columnValue.length() == 0) {
                name = type.getElementSimpleName(element);
            } else {
                name = columnValue;
            }
        }

        final boolean isPrimaryKey;
        final boolean isAutoincrement;
        {
            final PrimaryKey primaryKey = type.getElementAnnotation(element, PrimaryKey.class);
            isPrimaryKey = primaryKey != null;
            isAutoincrement = isPrimaryKey && primaryKey.autoincrement();
        }

        final int versionWhenAdded;
        {
            final NewColumn newColumn = type.getElementAnnotation(element, NewColumn.class);
            versionWhenAdded = newColumn != null ? newColumn.value() : 0;
        }

        final boolean isUnique;
        {
            final Unique unique = type.getElementAnnotation(element, Unique.class);
            isUnique = unique != null;
        }

        final String defValue;
        {
            final Default def = type.getElementAnnotation(element, Default.class);
            defValue = def != null ? def.value() : null;
        }

        final boolean isNotNull;
        {
            final SQLiteNotNull sqLiteNotNull = type.getElementAnnotation(element, SQLiteNotNull.class);
            isNotNull = sqLiteNotNull != null;
        }

        final StormSchemeIndex index;
        {
            final Index indexAnnotation = type.getElementAnnotation(element, Index.class);
            if (indexAnnotation != null) {
                index = new StormSchemeIndex(indexAnnotation.value(), indexAnnotation.sorting().name());
            } else {
                index = null;
            }
        }

        final StormSchemeForeignKey foreignKey;
        {
            final ForeignKey foreignKeyAnnotation = type.getElementAnnotation(element, ForeignKey.class);
            if (foreignKeyAnnotation != null) {

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
            } else {
                foreignKey = null;
            }
        }

        final StormType stormType = parseType(type, main, element);

        return new StormSchemeColumn()
                .setColumnName(name)
                .setIsPrimaryKey(isPrimaryKey)
                .setIsAutoIncrement(isAutoincrement)
                .setVersionWhenAdded(versionWhenAdded)
                .setIsUnique(isUnique)
                .setDefaultValue(defValue)
                .setIsNonNull(isNotNull)
                .setIndex(index)
                .setForeignKey(foreignKey)
                .setType(stormType);
    }

    <MAIN, ELEMENT, TYPE> StormType parseType(
            StormSchemeType<MAIN, ELEMENT, TYPE> type,
            MAIN main,
            ELEMENT element
    ) throws StormSchemeException {

        final Serialize serialize = type.getElementAnnotation(element, Serialize.class);

        final StormType stormType;
        if (serialize == null) {
            stormType = type.parseType(type.getElementType(element));
        } else {
            StormType serializerType = null;
            try {
                serializerType = type.parseType(type.getSerializerType(serialize));
            } catch (StormSchemeType.SerializerNotOfTypeException e) {
                throw StormSchemeException.newInstance("Field `%s` in class `%s` has registered Serializer, " +
                                "but it's not of type `StormSerializer`",
                        type.getElementSimpleName(element), main
                );
            } catch (StormSchemeType.SerializerTypeNotSqliteType e) {
                throw StormSchemeException.newInstance("Field `%s` in class `%s` has registered serializer, " +
                                "but it's not a valid one. Check type parameters of a supplied serializer",
                        type.getElementSimpleName(element), main
                );
            } catch (StormSchemeType.SerializerWrongTypeArgumentsException e) {
                throw StormSchemeException.newInstance("Field `%s` in class `%s` has registered serializer " +
                                "with OUT type not supported by SQLite",
                        type.getElementSimpleName(element), main
                );
            }
            stormType = serializerType;
        }

        if (stormType == null) {
            if (type.isOfTypeBoolean(type.getElementType(element))) {
                throw StormSchemeException.newInstance("Field `%s` in `%s`" +
                        " is of type boolean, but has no registered Serializer. In order to store boolean" +
                        " in SQLite you must specify one.", type.getElementSimpleName(element), main);
            }

            throw StormSchemeException.newInstance("Cannot parse type for `%s` in `%s`", type.getElementSimpleName(element), main);
        }

        return stormType;
    }
}
