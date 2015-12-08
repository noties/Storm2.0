package storm.scheme;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import storm.annotations.NewColumn;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;

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

        final List<String> fieldsCreateStatements = new ArrayList<>();

        ColumnHolder columnHolder;
        boolean hasPrimaryKey = false;

        for (Field field: fields) {
            columnHolder = getFieldCreateStatement(field);
            hasPrimaryKey |= columnHolder.isPrimaryKey;
            fieldsCreateStatements.add(columnHolder.createStatement);
        }

        if (!hasPrimaryKey) {
            throw new StormSchemeException("Class `" + cl.getName() + "` has no primary key." +
                    " Field must be annotated with @PrimaryKey");
        }

        if (fieldsCreateStatements.size() == 0) {
            throw new StormSchemeException("Class `" + cl.getName() + "` has no columns creation statements.");
        }
    }

    private static String getTableName(Class<?> cl) throws StormSchemeException {

        final Table table = cl.getAnnotation(Table.class);
        if (table == null) {
            throw new StormSchemeException("Class `" + cl.getName() + "` must be annotated with @Table" +
                    " in order to build StormScheme");
        }

        return SchemeTextUtils.ifNotEmpty(table.value(), cl.getSimpleName());
    }

    private static ColumnHolder getFieldCreateStatement(Field field) throws StormSchemeException {

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        final boolean isPrimaryKey = field.getAnnotation(PrimaryKey.class) != null;
        final boolean isAutoincrement = field.getAnnotation()

        final int version;
        {
            final NewColumn newColumn = field.getAnnotation(NewColumn.class);
            if (newColumn != null) {
                version = newColumn.value();
            } else {
                version = 0;
            }
        }

        throw new StormSchemeException("");
    }

    private static class ColumnHolder {

        final boolean isPrimaryKey;
        final String createStatement;
        String indexStatement;
        int version;

        private ColumnHolder(boolean isPrimaryKey, String createStatement) {
            this.isPrimaryKey = isPrimaryKey;
            this.createStatement = createStatement;
        }
    }
}
