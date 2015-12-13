package storm.parser;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public interface StormParser<T> {

    T fromCursor(Cursor cursor);

    // if this ContentValues would be used to update existing row
    // and if primary key would be included in this ContentValues
    // there will be an exception
    ContentValues toContentValues(T value, boolean putPrimaryKey);

}
