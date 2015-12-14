package storm.scheme;

import java.util.List;

/**
 * Created by Dimitry Ivanov on 09.12.2015.
 */
class StormSchemeTable {

    private final String mTableName;
    private final List<StormSchemeColumn> mColumns;

    private int mVersionWhenAdded;

    public StormSchemeTable(String tableName, List<StormSchemeColumn> columns) {
        mTableName = tableName;
        mColumns = columns;
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

    public StormSchemeTable setVersionWhenAdded(int versionWhenAdded) {
        mVersionWhenAdded = versionWhenAdded;
        return this;
    }
}
