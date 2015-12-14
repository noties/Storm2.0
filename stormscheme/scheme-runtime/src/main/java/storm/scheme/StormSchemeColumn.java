package storm.scheme;

import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 09.12.2015.
 */
class StormSchemeColumn {

    private StormType mType;
    private String mColumnName;

    private boolean mIsPrimaryKey;
    private boolean mIsAutoIncrement;
    private boolean mIsNonNull;
    private boolean mIsUnique;

    private String mDefaultValue;

    private StormSchemeIndex mIndex;
    private StormSchemeForeignKey mForeignKey;

    private int mVersionWhenAdded;

    public StormType getType() {
        return mType;
    }

    public StormSchemeColumn setType(StormType type) {
        mType = type;
        return this;
    }

    public String getColumnName() {
        return mColumnName;
    }

    public StormSchemeColumn setColumnName(String columnName) {
        mColumnName = columnName;
        return this;
    }

    public boolean isPrimaryKey() {
        return mIsPrimaryKey;
    }

    public StormSchemeColumn setIsPrimaryKey(boolean isPrimaryKey) {
        mIsPrimaryKey = isPrimaryKey;
        return this;
    }

    public boolean isAutoIncrement() {
        return mIsAutoIncrement;
    }

    public StormSchemeColumn setIsAutoIncrement(boolean isAutoIncrement) {
        mIsAutoIncrement = isAutoIncrement;
        return this;
    }

    public boolean isNonNull() {
        return mIsNonNull;
    }

    public StormSchemeColumn setIsNonNull(boolean isNonNull) {
        mIsNonNull = isNonNull;
        return this;
    }

    public boolean isUnique() {
        return mIsUnique;
    }

    public StormSchemeColumn setIsUnique(boolean isUnique) {
        mIsUnique = isUnique;
        return this;
    }

    public String getDefaultValue() {
        return mDefaultValue;
    }

    public StormSchemeColumn setDefaultValue(String defaultValue) {
        mDefaultValue = defaultValue;
        return this;
    }

    public StormSchemeIndex getIndex() {
        return mIndex;
    }

    public StormSchemeColumn setIndex(StormSchemeIndex index) {
        mIndex = index;
        return this;
    }

    public StormSchemeForeignKey getForeignKey() {
        return mForeignKey;
    }

    public StormSchemeColumn setForeignKey(StormSchemeForeignKey foreignKey) {
        mForeignKey = foreignKey;
        return this;
    }

    public int getVersionWhenAdded() {
        return mVersionWhenAdded;
    }

    public StormSchemeColumn setVersionWhenAdded(int versionWhenAdded) {
        mVersionWhenAdded = versionWhenAdded;
        return this;
    }
}
