package storm.parser.metadata;

import android.net.Uri;

import java.lang.reflect.Field;

import storm.parser.metadata.PrimaryKeySelection;
import storm.parser.metadata.StormMetadata;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
class StormMetadataRuntime<T> implements StormMetadata<T> {

    private final String mTableName;
    private final boolean mIsPrimaryKeyAutoincrement;
    private final Uri mNotificationUri;
    private final String mPrimaryKeyName;
    private final Field mPrimaryKeyField;

    StormMetadataRuntime(
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
    public String tableName() {
        return mTableName;
    }

    @Override
    public Uri notificationUri() {
        return mNotificationUri;
    }

    @Override
    public boolean isPrimaryKeyAutoincrement() {
        return mIsPrimaryKeyAutoincrement;
    }

    @Override
    public PrimaryKeySelection primaryKeySelection(T value) {
        try {
            final Object primaryKeyValue = mPrimaryKeyField.get(value);
            return new PrimaryKeySelection(mPrimaryKeyName, primaryKeyValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
