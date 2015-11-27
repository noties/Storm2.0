package storm.iterator;

import android.database.Cursor;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Dimitry Ivanov on 20.09.2015.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CursorIteratorCachedTest extends TestCase {

    @Test
    public void testCached() {

        final int count = 10;
        final int cacheSize = 5;

        final Object[] objects = new Object[count];
        for (int i = 0; i < count; i++) {
            objects[i] = new Object();
        }

        final StormCursorIteratorParserMock cursorParserMock = new StormCursorIteratorParserMock(objects);
        final StormIteratorCachedMock iterator = new StormIteratorCachedMock(
                new CursorMock() {
                    @Override
                    public int getCount() {
                        return count;
                    }

                    @Override
                    public boolean moveToPosition(int position) {
                        return position < count;
                    }
                },
                cursorParserMock,
                cacheSize
        );

        assertTrue(iterator.getCacheSize() == 5);

        for (int i = 0; i < count; i++) {
            assertTrue(!iterator.contains(i));
        }

        for (int i = 0; i < cacheSize + 1; i++) {
            assertEquals(iterator.get(i), objects[i]);
            assertTrue(iterator.contains(i));
        }

        assertTrue(!iterator.contains(0));

        iterator.clearCache();
        for (int i = 0; i < count; i++) {
            assertTrue(!iterator.contains(i));
        }
    }

    private static class StormIteratorCachedMock extends CursorIteratorCachedImpl<Object> {

        private final StormCursorIteratorParserMock mCursorParser;

        public StormIteratorCachedMock(Cursor cursor, StormCursorIteratorParserMock cursorParser, int cachedSize) {
            super(cursor, null, cachedSize);
            this.mCursorParser = cursorParser;
        }

        @Override
        protected Object getAfterCursorChecked(int position) {
            Object o = getCache().get(position);
            if (o == null) {
                o = mCursorParser.get(position);
                getCache().put(position, o);
            }
            return o;
        }

        boolean contains(int position) {
            return getCache().get(position) != null;
        }
    }

    private static class StormCursorIteratorParserMock implements CursorIteratorParser<Object> {

        private final Object[] mObjects;

        StormCursorIteratorParserMock(Object[] objects) {
            this.mObjects = objects;
        }

        @Override
        public Object parse(Cursor cursor) {
            return null;
        }

        public Object get(int position) {
            return mObjects[position];
        }
    }
}
