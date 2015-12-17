package storm.parser;

import android.net.Uri;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public interface StormTableMetadata<T> {

    String getTableName();
    Uri getNotificationUri();
    boolean isPrimaryKeyAutoincrement();
    PrimaryKeySelection getPrimaryKeySelection(T value);

}
