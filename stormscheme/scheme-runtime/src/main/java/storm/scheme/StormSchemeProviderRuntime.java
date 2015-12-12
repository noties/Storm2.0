package storm.scheme;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import storm.annotations.Column;
import storm.annotations.Default;
import storm.annotations.ForeignKey;
import storm.annotations.Index;
import storm.annotations.NewColumn;
import storm.annotations.PrimaryKey;
import storm.annotations.Serialize;
import storm.annotations.Table;
import storm.annotations.Unique;
import storm.reflect.TypeResolverLight;
import storm.serializer.StormSerializer;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 02.12.2015.
 */
class StormSchemeProviderRuntime implements StormSchemeProvider {

    @Override
    public StormScheme provide(Class<?> cl) throws StormSchemeException {

        final String tableName = getTableName(cl);

        final Field[] fields = cl.getDeclaredFields();
        if (fields == null
                || fields.length == 0) {
            throw new StormSchemeException("Class `" + cl.getName() + "` has no fields");
        }

        final List<StormSchemeColumn> columns = new ArrayList<>();

        StormSchemeColumn column;
        boolean hasPrimaryKey = false;

        for (Field field: fields) {
            column = getFieldCreateStatement(field);
            // this field should not be included
            if (column == null) {
                continue;
            }
            hasPrimaryKey |= column.isPrimaryKey();
            columns.add(column);
        }

        if (!hasPrimaryKey) {
            throw new StormSchemeException("Class `" + cl.getName() + "` has no primary key." +
                    " Field must be annotated with @PrimaryKey");
        }

        if (columns.size() == 0) {
            throw new StormSchemeException("Class `" + cl.getName() + "` has no columns creation statements.");
        }

        return new StormSchemeStatementsGenerator(new StormSchemeTable(tableName, columns));
    }

    static String getTableName(Class<?> cl) throws StormSchemeException {

        final Table table = cl.getAnnotation(Table.class);
        if (table == null) {
            throw new StormSchemeException("Class `" + cl.getName() + "` must be annotated with @Table" +
                    " in order to build StormScheme");
        }

        return SchemeTextUtils.ifNotEmpty(table.value(), cl.getSimpleName());
    }

    static StormSchemeColumn getFieldCreateStatement(Field field) throws StormSchemeException {

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        if (!isFieldShouldBeParsed(field)) {
            return null;
        }

        final String name;
        {
            final Column column = field.getAnnotation(Column.class);

            if (column == null) {
                throw StormSchemeException.newInstance("Field `%s` in class `%s` should be included " +
                        "in table scheme, but is not annotated with `@Column`. " +
                        "If field should not be included add `transient` modifier. " +
                        "Else annotate with `@Column` (even if field is already annotated with `@PrimaryKey`)",
                        field.getName(), field.getDeclaringClass()
                );
            }

            final String columnValue = column.value();
            if (SchemeTextUtils.isEmpty(columnValue)) {
                name = field.getName();
            } else {
                name = columnValue;
            }
        }

        final StormType type = parseType(field);

        final boolean isPrimaryKey;
        final boolean isAutoincrement;
        {
            final PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            isPrimaryKey = primaryKey != null;
            isAutoincrement = isPrimaryKey && primaryKey.autoincrement();
        }

        final int versionWhenAdded;
        {
            final NewColumn newColumn = field.getAnnotation(NewColumn.class);
            if (newColumn != null) {
                versionWhenAdded = newColumn.value();
            } else {
                versionWhenAdded = 0;
            }
        }

        final boolean isUnique;
        {
            isUnique = field.getAnnotation(Unique.class) != null;
        }

        final String defValue;
        {
            final Default def = field.getAnnotation(Default.class);
            defValue = def != null ? def.value() : null;
        }

        final StormSchemeIndex index = parseIndex(field);

        final StormSchemeForeignKey foreignKey = parseForeignKey(field);

        return new StormSchemeColumn()
                .setIsPrimaryKey(isPrimaryKey)
                .setIsAutoIncrement(isAutoincrement)
                .setColumnName(name)
                .setType(type)
                .setVersionWhenAdded(versionWhenAdded)
                .setIsUnique(isUnique)
                .setDefaultValue(defValue)
                .setIndex(index)
                .setForeignKey(foreignKey);
    }

    static boolean isFieldShouldBeParsed(Field field) {
        return !Modifier.isTransient(field.getModifiers());
    }

    static StormType parseType(Field f) throws StormSchemeException {

        final StormType type;

        // first check if we have serializer
        final Serialize serialize = f.getAnnotation(Serialize.class);
        if (serialize == null) {
            type = StormType.forValue(f.getType());
        } else {
            type = parseSerializedType(f, serialize);
        }

        if (type == null) {
            // we could not parse type, indicate an error
            // if it's boolean, then it could be missing serializer
            final Class<?> fieldType = f.getType();
            if (Boolean.TYPE.equals(fieldType)
                    || Boolean.class.equals(fieldType)) {
                throw new StormSchemeException("Field `" + f.getName() + "` in `" + f.getDeclaringClass() + "`" +
                        " is of type boolean, but has no registered Serializer. In order to store boolean" +
                        " in SQLite you must specify one.");
            }

            throw new StormSchemeException("Field's type `" + f.getName() + "` in `" + f.getDeclaringClass() + "`" +
                    " could not be parsed. Found type: `" + fieldType + "`");
        }

        return type;
    }

    static StormType parseSerializedType(Field f, Serialize serialize) throws StormSchemeException {

        // obtain serializer class
        final Class<?> serializerClass = serialize.value();

        // check if it implements `StormSerializer`
        if (!StormSerializer.class.isAssignableFrom(serializerClass)) {
            throw StormSchemeException.newInstance("Field `%s` in class `%s` has registered Serializer, " +
                    "but it's not of type `StormSerializer`. Serializer: `%s`",
                    f.getName(), f.getDeclaringClass(), serializerClass
            );
        }

        // obtain type parameters & check them
        // IN should be of field's type & OUT should SQLite supported type
        final Class<?>[] typeParams = TypeResolverLight.resolveRawArguments(StormSerializer.class, serializerClass);
        if (TypeResolverLight.Unknown.class.equals(typeParams[0])
                || TypeResolverLight.Unknown.class.equals(typeParams[1])) {
            throw StormSchemeException.newInstance("Field `%s` in class `%s` has registered serializer, " +
                    "but it's not a valid one. Check type parameters of a class `%s`",
                    f.getName(), f.getDeclaringClass(), serializerClass
            );
        }

        // we need to insert this check, but also to check if field is primitive
        // as long as interface could not hold a primitive type it will always be boxed one
//        // check if IN is the same as field's type
//        if (!f.getType().equals(typeParams[0])) {
//            throw StormSchemeException.newInstance("Field `%s` in class `%s` has registered serializer, " +
//                    "but it's IN value is not the same of field's one. Expected: %s, actual: %s",
//                    f.getName(), f.getDeclaringClass(), f.getType(), typeParams[0]
//            );
//        }

        // now, check if OUT is supported
        final StormType type = StormType.forValue(typeParams[1]);
        if (type == null) {
            throw StormSchemeException.newInstance("Field `%s` in class `%s` has registered serializer " +
                    "with OUT type not supported by SQLite. Type: %s",
                    f.getName(), f.getClass(), typeParams[1]
            );
        }

        return type;
    }

    static StormSchemeIndex parseIndex(Field f) {
        final Index index = f.getAnnotation(Index.class);
        if (index == null) {
            return null;
        }
        return new StormSchemeIndex(index.value(), index.sorting().name());
    }

    static StormSchemeForeignKey parseForeignKey(Field f) {
        final ForeignKey foreignKey = f.getAnnotation(ForeignKey.class);
        if (foreignKey == null) {
            return null;
        }

        final ForeignKey.ForeignKeyAction onUpdate = foreignKey.onUpdate();
        final ForeignKey.ForeignKeyAction onDelete = foreignKey.onDelete();

        final StormSchemeForeignKey key = new StormSchemeForeignKey(
                foreignKey.parentTable(),
                foreignKey.parentColumnName()
        );

        if (onUpdate != ForeignKey.ForeignKeyAction.NO_ACTION) {
            key.setOnUpdateAction(onUpdate.name());
        }

        if (onDelete != ForeignKey.ForeignKeyAction.NO_ACTION) {
            key.setOnDeleteAction(onDelete.name());
        }

        return key;
    }
}
