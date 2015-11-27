package storm.iterator;

import android.database.Cursor;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CursorIteratorBaseTest extends TestCase {

    @Test
    public void testNullCursor() {

        final CursorIterator<Object> iterator = new CursorIteratorBaseImpl<>(
                null,
                null
        );

        assertTrue(iterator.isClosed());
        assertTrue(iterator.getCount() == 0);
        assertTrue(!iterator.hasNext());

        try {
            iterator.get(0);
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }

        try {
            iterator.next();
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testRemove() {
        final CursorIterator<Object> iterator = new CursorIteratorBaseImpl<>(
                null,
                null
        );

        try {
            iterator.remove();
            assertTrue(false);
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }
    }

    public void testBaseCursor() {
        final CursorIterator<Object> iterator = new CursorIteratorBaseImpl<>(
                new CursorMock() {

                    private boolean mIsClosed;

                    @Override
                    public int getCount() {
                        return 5;
                    }

                    @Override
                    public boolean moveToPosition(int position) {
                        return position < 5;
                    }

                    @Override
                    public void close() {
                        mIsClosed = true;
                    }

                    @Override
                    public boolean isClosed() {
                        return mIsClosed;
                    }
                },
                null
        );

        assertTrue(iterator.hasNext());
        assertTrue(!iterator.isClosed());
        assertTrue(iterator.getCount() == 5);

        try {
            iterator.get(5);
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }

        iterator.close();

        assertTrue(!iterator.hasNext());
        assertTrue(iterator.isClosed());
        assertTrue(iterator.getCount() == 0);

        try {
            iterator.next();
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testWithParser() {
        final CursorIterator<Object> iterator = new CursorIteratorBaseImpl<>(
                new CursorMock() {
                    @Override
                    public int getCount() {
                        return 3;
                    }

                    @Override
                    public boolean moveToPosition(int position) {
                        return position < 3;
                    }
                },
                new CursorIteratorParser<Object>() {
                    @Override
                    public Object parse(Cursor cursor) {
                        return new Object();
                    }
                }
        );

        Object o;
        for (int i = 0, count = iterator.getCount(); i < count; i++) {
            o = iterator.get(i);
            assertTrue(o != null);
        }

        try {
            iterator.get(3);
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testForEach() {
        final CursorIterator<Object> iterator = new CursorIteratorBaseImpl<>(
                new CursorMock() {
                    @Override
                    public int getCount() {
                        return 2;
                    }

                    @Override
                    public boolean moveToPosition(int position) {
                        return position < 2;
                    }
                },
                new CursorIteratorParser<Object>() {
                    @Override
                    public Object parse(Cursor cursor) {
                        return new Object();
                    }
                }
        );

        int iterated = 0;
        for (Object o: iterator) {
            iterated++;
        }
        assertTrue(iterated == 2);
    }

    @Test
    public void testNullParser() {
        final CursorIterator<Object> iterator = new CursorIteratorBaseImpl<>(
                new CursorMock() {
                    @Override
                    public int getCount() {
                        return 4;
                    }

                    @Override
                    public boolean moveToPosition(int position) {
                        return position < 4;
                    }
                },
                null
        );

        try {
            for (Object o: iterator) {
                ;
            }
            assertTrue(false);
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }
}