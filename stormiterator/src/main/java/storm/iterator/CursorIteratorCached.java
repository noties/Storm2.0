package storm.iterator;

/**
 * Created by Dimitry Ivanov on 05.08.2015.
 */
public interface CursorIteratorCached<T> extends CursorIterator<T> {
    void clearCache();
    int getCacheSize();
}
