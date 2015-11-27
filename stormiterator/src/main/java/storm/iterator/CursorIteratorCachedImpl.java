package storm.iterator;

import android.database.Cursor;
import android.util.LruCache;

/**
 * Created by Dimitry Ivanov on 19.09.2015.
 */
public class CursorIteratorCachedImpl<T> extends CursorIteratorBaseImpl<T> implements CursorIteratorCached<T> {

    private final CacheImpl<T> mCache;

    public CursorIteratorCachedImpl(Cursor cursor, CursorIteratorParser<T> cursorIteratorParser, int cachedSize) {
        super(cursor, cursorIteratorParser);
        mCache = new CacheImpl<T>(cachedSize);
    }

    @Override
    protected T getAfterCursorChecked(int position) {
        T cachedValue = mCache.get(position);
        if (cachedValue == null) {
            // obtain an object from CursorParser
            cachedValue = super.getAfterCursorChecked(position);
            mCache.put(position, cachedValue);
        }
        return cachedValue;
    }

    @Override
    public void clearCache() {
        mCache.evictAll();
    }

    @Override
    public int getCacheSize() {
        return mCache.maxSize();
    }

    protected CacheImpl<T> getCache() {
        return mCache;
    }

    protected static class CacheImpl<T> extends LruCache<Integer, T> {

        public CacheImpl(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(Integer key, T value) {
            return 1;
        }
    }
}
