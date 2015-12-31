package storm.scheme;

import java.util.List;

/**
 * Created by Dimitry Ivanov on 09.12.2015.
 */
class StormSchemeTable {

    private final String mTableName;
    private final List<StormSchemeColumn> mColumns;
    private final int mVersionWhenAdded;
    private final boolean mIsRecreateOnUpgrade;

    public StormSchemeTable(
            String tableName,
            List<StormSchemeColumn> columns,
            int versionWhenAdded,
            boolean isRecreateOnUpgrade
    ) {
        mTableName = tableName;
        mColumns = columns;
        mVersionWhenAdded = versionWhenAdded;
        mIsRecreateOnUpgrade = isRecreateOnUpgrade;
    }

    public String getTableName() {
        return mTableName;
    }

    public List<StormSchemeColumn> getColumns() {
        return mColumns;
    }

    public int getVersionWhenAdded() {
        return mVersionWhenAdded;
    }

    public boolean isRecreateOnUpgrade() {
        return mIsRecreateOnUpgrade;
    }

    public StormSchemeTable setColumns(List<StormSchemeColumn> columns) {
        return new StormSchemeTable(mTableName, columns, mVersionWhenAdded, mIsRecreateOnUpgrade);
    }
}
