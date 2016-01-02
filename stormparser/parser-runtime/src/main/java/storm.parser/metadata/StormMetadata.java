package storm.parser.metadata;

import android.net.Uri;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public interface StormMetadata<T> {

    String tableName();
    Uri notificationUri();
    boolean isPrimaryKeyAutoincrement();
    PrimaryKeySelection primaryKeySelection(T value);

}
