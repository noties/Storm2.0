package storm.parser.scheme;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import storm.parser.StormParserColumn;
import storm.parser.StormParserTable;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 09.12.2015.
 */
class StormSchemeRuntime implements StormScheme {

    private static final String CREATE_TABLE_PATTERN = "CREATE TABLE %1$s(%2$s);";
    private static final String CREATE_INDEX_PATTERN = "CREATE INDEX %1$s ON %2$s(%3$s %4$s);";

    private static final String ALTER_TABLE_PATTERN = "ALTER TABLE %1$s ADD COLUMN %2$s;";

    private static final String REFERENCES_PATTERN = "REFERENCES %1$s(%2$s)";

    private static final String DROP_TABLE_PATTERN = "DROP TABLE %1$s IF EXISTS;";

    private final StormParserTable<Class<?>, Field, Class<?>> mTable;

    public StormSchemeRuntime(StormParserTable<Class<?>, Field, Class<?>> table) {
        mTable = table;
    }

    @Override
    public List<String> onCreate() throws StormSchemeException {

        final String tableName = mTable.getTableName();

        final List<String> createStatements = new ArrayList<>();
        final List<String> indexStatements  = new ArrayList<>();

        StormSchemeIndex index;

        for (StormParserColumn<Field, Class<?>> column: mTable.getElements()) {

            createStatements.add(getColumnCreateStatement(column));

            index = column.getIndex();
            if (index != null) {
                indexStatements.add(getColumnIndexStatement(tableName, column.getName(), index));
            }
        }

        if (createStatements.size() == 0) {
            throw new StormSchemeException("Table with name `" + tableName + "` has no create statements");
        }

        return getOnCreateStatement(tableName, createStatements, indexStatements);
    }

    @Override
    public List<String> onUpgrade(int oldVersion, int newVersion) throws StormSchemeException {

        // if table is marked as `recreateOnUpgrade` then we have to exec DROP & then full ON CREATE
        // it really doesn't matter what version we have
        if (mTable.isRecreateOnUpgrade()) {
            final List<String> onCreate = onCreate();
            // insert at first position
            onCreate.add(0, String.format(DROP_TABLE_PATTERN, mTable.getTableName()));
            return onCreate;
        }

        final UpgradeChecker upgradeChecker = new UpgradeChecker(oldVersion, newVersion);

        // if table is annotated with @NewTable && versionWhenAdded isUpgrade
        // then create table -> include all fields, that isUpgrade

        final String tableName = mTable.getTableName();
        final List<String> list;

        StormSchemeIndex index;

        if (upgradeChecker.isUpgrade(mTable.getVersionWhenAdded())) {

            final List<String> schemeStatements = new ArrayList<>();
            final List<String> indexStatements = new ArrayList<>();

            int versionWhenAdded;

            for (StormParserColumn<Field, Class<?>> column: mTable.getElements()) {

                versionWhenAdded = column.getVersionWhenAdded();

                // this condition should add all columns
                // with version when added == 0 || when column is annotated with NewColumn
                // not bigger than current newVersion
                if (versionWhenAdded == 0 || upgradeChecker.isUpgrade(versionWhenAdded)) {

                    schemeStatements.add(getColumnCreateStatement(column));

                    index = column.getIndex();
                    if (index != null) {
                        indexStatements.add(getColumnIndexStatement(tableName, column.getName(), index));
                    }
                }

            }

            list = getOnCreateStatement(tableName, schemeStatements, indexStatements);

        } else {

            final List<String> alterStatements = new ArrayList<>();

            for (StormParserColumn<Field, Class<?>> column: mTable.getElements()) {

                if (upgradeChecker.isUpgrade(column.getVersionWhenAdded())) {

                    alterStatements.add(getAlterTableAddColumnStatement(tableName, column));

                    index = column.getIndex();
                    if (index != null) {
                        alterStatements.add(getColumnIndexStatement(tableName, column.getName(), index));
                    }
                }
            }

            list = alterStatements;
        }


        if (list.size() == 0) {
            return null;
        }

        return list;
    }

    static String getColumnCreateStatement(StormParserColumn<Field, Class<?>> column) {

        final StringBuilder builder = new StringBuilder();

        builder.append(column.getName())
                .append(" ")
                .append(convertTypeToSql(column.getType()));

        if (column.isPrimaryKey()) {
            builder.append(" PRIMARY KEY");
        }

        if (column.isAutoIncrement()) {
            builder.append(" AUTOINCREMENT");
        }

        if (column.isNonNull()) {
            builder.append(" NOT NULL");
        }

        if (!isTextEmpty(column.getDefValue())) {
            builder.append(" DEFAULT ")
                    .append(column.getDefValue());
        }

        if (column.isUnique()) {
            builder.append(" UNIQUE");
        }

        final StormSchemeForeignKey foreignKey = column.getForeignKey();
        if (foreignKey != null) {
            builder.append(" ")
                    .append(getForeignKeyStatement(foreignKey));
        }

        return builder.toString();
    }

    static String getAlterTableAddColumnStatement(String tableName, StormParserColumn<Field, Class<?>> column) {
        return String.format(ALTER_TABLE_PATTERN, tableName, getColumnCreateStatement(column));
    }

    static String getForeignKeyStatement(StormSchemeForeignKey foreignKey) {

        final StringBuilder builder = new StringBuilder();

        builder.append(String.format(REFERENCES_PATTERN, foreignKey.getParentTable(), foreignKey.getParentColumn()));

        if (!isTextEmpty(foreignKey.getOnUpdateAction())) {
            builder.append(" ON UPDATE ")
                    .append(foreignKey.getOnUpdateAction());
        }

        if (!isTextEmpty(foreignKey.getOnDeleteAction())) {
            builder.append(" ON DELETE ")
                    .append(foreignKey.getOnDeleteAction());
        }

        return builder.toString();
    }

    static String getColumnIndexStatement(String tableName, String columnName, StormSchemeIndex index) {
        return String.format(
                CREATE_INDEX_PATTERN,
                index.getIndexName(),
                tableName,
                columnName,
                index.getSorting()
        );
    }

    static List<String> getOnCreateStatement(String tableName, List<String> columns, List<String> indexes) {

        final StringBuilder onCreateColumns = new StringBuilder();
        boolean isFirst = true;
        for (String column: columns) {
            if (isFirst) {
                isFirst = false;
            } else {
                onCreateColumns.append(", ");
            }
            onCreateColumns.append(column);
        }

        final String onCreateStatement = String.format(CREATE_TABLE_PATTERN, tableName, onCreateColumns.toString());
        if (indexes == null) {
            return Collections.singletonList(onCreateStatement);
        }

        final List<String> statements = new ArrayList<>();
        statements.add(onCreateStatement);
        statements.addAll(indexes);

        return statements;
    }

    static String convertTypeToSql(StormType type) {

        switch (type) {

            case INT:
            case LONG:
                return "INTEGER";

            case FLOAT:
            case DOUBLE:
                return "REAL";

            case STRING:
                return "TEXT";

            case BYTE_ARRAY:
                return "BLOB";

            default:
                throw new IllegalStateException("Unknown or unsupported type: " + type);
        }
    }

    private static class UpgradeChecker {

        private final int oldVersion;
        private final int newVersion;

        private UpgradeChecker(int oldVersion, int newVersion) {
            this.oldVersion = oldVersion;
            this.newVersion = newVersion;
        }

        boolean isUpgrade(int versionWhenAdded) {
            return versionWhenAdded > oldVersion && versionWhenAdded <= newVersion;
        }
    }

    private static boolean isTextEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }
}
