package storm.core;

import android.database.Cursor;

import java.util.List;

import storm.iterator.CursorIterator;
import storm.iterator.CursorIteratorCached;
import storm.query.Query;

/**
 * Created by Dimitry Ivanov on 16.12.2015.
 */
public interface StormQueryDispatcher extends StormDispatcher {

    Cursor asCursor(Storm storm, Query query);

    <T extends StormObject> T       asOne   (Storm storm, Class<T> table, Query query);
    <T extends StormObject> List<T> asList  (Storm storm, Class<T> table, Query query);

    <T extends StormObject> CursorIterator<T>       asIterator      (Storm storm, Class<T> table, Query query);
    <T extends StormObject> CursorIteratorCached<T> asCachedIterator(Storm storm, Class<T> table, Query query, int cacheSize);

}
