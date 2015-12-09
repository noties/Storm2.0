package storm.scheme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 09.12.2015.
 */
public class StormSchemeStatementsGenerator implements StormScheme {

    private static final String CREATE_TABLE_PATTERN = "CREATE TABLE %1$s(%2$s);";
    private static final String CREATE_INDEX_PATTERN = "CREATE INDEX %1$s ON %2$s(%3$s %4$s);";

    private static final String ALTER_TABLE_PATTERN = "ALTER TABLE %1$s ADD COLUMN %2$s";

    private static final String REFERENCES_PATTERN = "REFERENCES %1$s(%2$s)";

    private final StormSchemeTable mTable;

    public StormSchemeStatementsGenerator(StormSchemeTable table) {
        mTable = table;
    }

    @Override
    public List<String> onCreate() throws StormSchemeException {

        final String tableName = mTable.getTableName();

        final List<String> createStatements = new ArrayList<>();
        final List<String> indexStatements  = new ArrayList<>();

        StormSchemeIndex index;

        for (StormSchemeColumn column: mTable.getColumns()) {

            createStatements.add(getColumnCreateStatement(column));

            index = column.getIndex();
            if (index != null) {
                indexStatements.add(getColumnIndexStatement(tableName, column.getColumnName(), index));
            }
        }

        if (createStatements.size() == 0) {
            throw new StormSchemeException("Table with name `" + tableName + "` has no create statements");
        }

        return getOnCreateStatement(tableName, createStatements, indexStatements);
    }

    @Override
    public List<String> onUpgrade(int oldVersion, int newVersion) throws StormSchemeException {

        final UpgradeChecker upgradeChecker = new UpgradeChecker(oldVersion, newVersion);

        // first check if the whole table is new
        // if it is, when add all the create statements
        if (upgradeChecker.isUpgrade(mTable.getVersionWhenAdded())) {
            return onCreate();
        }

        final String tableName = mTable.getTableName();

        final List<String> list = new ArrayList<>();

        StormSchemeIndex index;

        for (StormSchemeColumn column: mTable.getColumns()) {

            if (upgradeChecker.isUpgrade(column.getVersionWhenAdded())) {

                list.add(String.format(ALTER_TABLE_PATTERN, tableName, getColumnCreateStatement(column)));

                index = column.getIndex();
                if (index != null) {
                    list.add(getColumnIndexStatement(tableName, column.getColumnName(), index));
                }
            }
        }

        if (list.size() == 0) {
            return null;
        }

        return list;
    }

    static String getColumnCreateStatement(StormSchemeColumn column) {

        final StringBuilder builder = new StringBuilder();

        builder.append(column.getColumnName())
                .append(" ")
                .append(convertTypeToSql(column.getType()));

        if (column.isPrimaryKey()) {
            builder.append(" PRIMARY KEY");
        }

        if (column.isAutoIncrement()) {
            builder.append(" AUTOINCREMENT");
        }

        if (column.isNonNull()) {
            builder.append(" NON NULL");
        }

        if (!SchemeTextUtils.isEmpty(column.getDefaultValue())) {
            builder.append(" DEFAULT ")
                    .append(column.getDefaultValue());
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

    static String getForeignKeyStatement(StormSchemeForeignKey foreignKey) {

        final StringBuilder builder = new StringBuilder();

        builder.append(String.format(REFERENCES_PATTERN, foreignKey.getParentTable(), foreignKey.getParentColumn()));

        if (!SchemeTextUtils.isEmpty(foreignKey.getOnUpdateAction())) {
            builder.append(" ON UPDATE ")
                    .append(foreignKey.getOnUpdateAction());
        }

        if (!SchemeTextUtils.isEmpty(foreignKey.getOnDeleteAction())) {
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
}
