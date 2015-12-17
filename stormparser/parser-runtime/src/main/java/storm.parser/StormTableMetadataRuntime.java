package storm.parser;

import android.net.Uri;

import java.lang.reflect.Field;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
class StormTableMetadataRuntime<T> implements StormTableMetadata<T> {

    private final String mTableName;
    private final boolean mIsPrimaryKeyAutoincrement;
    private final Uri mNotificationUri;
    private final String mPrimaryKeyName;
    private final Field mPrimaryKeyField;

    StormTableMetadataRuntime(
            String tableName,
            boolean isPrimaryKeyAutoincrement,
            Uri notificationUri,
            String primaryKeyName,
            Field primaryKeyField
    ) {
        mTableName = tableName;
        mIsPrimaryKeyAutoincrement = isPrimaryKeyAutoincrement;
        mNotificationUri = notificationUri;
        mPrimaryKeyName = primaryKeyName;
        mPrimaryKeyField = primaryKeyField;
    }

    @Override
    public String getTableName() {
        return mTableName;
    }

    @Override
    public Uri getNotificationUri() {
        return mNotificationUri;
    }

    @Override
    public boolean isPrimaryKeyAutoincrement() {
        return mIsPrimaryKeyAutoincrement;
    }

    @Override
    public PrimaryKeySelection getPrimaryKeySelection(T value) {
        try {
            final Object primaryKeyValue = mPrimaryKeyField.get(value);
            return new PrimaryKeySelection(mPrimaryKeyName, primaryKeyValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
