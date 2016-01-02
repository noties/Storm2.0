package storm.parser.metadata;

import android.net.Uri;

import storm.parser.StormParserItem;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public interface StormMetadata<T> extends StormParserItem<T> {

    String tableName();
    Uri notificationUri();
    boolean isPrimaryKeyAutoincrement();
    PrimaryKeySelection primaryKeySelection(T value);

}
