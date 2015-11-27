package storm.iterator;

import android.database.Cursor;

import java.util.Iterator;
import java.util.RandomAccess;

/**
 * Created by Dimitry Ivanov on 05.08.2015.
 */
public interface CursorIterator<T> extends Iterator<T>, Iterable<T>, RandomAccess {

    int getCount();
    Cursor getCursor();

    void close();
    boolean isClosed();

    T get(int position);

}
