package storm.scheme;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import storm.annotations.Column;
import storm.annotations.Default;
import storm.annotations.NewColumn;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.annotations.Unique;
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
            if ()
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

        throw new StormSchemeException("");
    }

    static boolean isFieldShouldBeParsed(Field field) {
        return !Modifier.isTransient(field.getModifiers());
    }

    static StormType parseType(Field field) {
        return null;
    }
}
