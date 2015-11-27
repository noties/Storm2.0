package storm.iterator;

import android.database.Cursor;

/**
 * Created by Dimitry Ivanov on 10.10.2015.
 */
public interface CursorIteratorParser<T> {
    T parse(Cursor cursor);
}
