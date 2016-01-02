package storm.parser.converter;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

import storm.parser.StormParserItem;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public interface StormConverter<T> extends StormParserItem<T> {

    T fromCursor(Cursor cursor);
    List<T> fromCursorList(Cursor cursor);

    // if this ContentValues would be used to update existing row
    // and if primary key would be included in this ContentValues
    // there will be an exception
    ContentValues toContentValues(T value, boolean putPrimaryKey);

}
