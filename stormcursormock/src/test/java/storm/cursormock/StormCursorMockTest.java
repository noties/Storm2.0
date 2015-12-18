package storm.cursormock;

import android.database.Cursor;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import storm.annotations.Column;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class StormCursorMockTest extends TestCase {

    private static class EmptyClass {}

    @Test
    public void testEmptyClass() {
        try {
            StormCursorMock.newInstance(EmptyClass.class);
            assertTrue(false);
        } catch (StormCursorMockException e) {
            assertTrue(true);
        }
    }


    private static class NotAnnotatedClass {
        String string;
        long someLong;
    }

    @Test
    public void testNotAnnotated() {
        try {
            StormCursorMock.newInstance(NotAnnotatedClass.class);
            assertTrue(false);
        } catch (StormCursorMockException e) {
            assertTrue(true);
        }
    }


    private static class SimpleClass {
        @Column
        long id;
        @Column
        String someString;
    }

    @Test
    public void testSimpleEmpty() {

        final Cursor cursor = StormCursorMock.newInstance(SimpleClass.class);

        assertEquals(cursor.getColumnCount(), 2);
        assertTrue(Arrays.equals(cursor.getColumnNames(), new String[]{"id", "someString"}));
        assertEquals(cursor.getColumnIndex("id"), 0);
        assertEquals(cursor.getColumnIndex("someString"), 1);
        assertEquals(cursor.getColumnIndex("notInThisCursor"), -1);

        assertEquals(cursor.getCount(), 0);
        assertFalse(cursor.moveToFirst());
        assertFalse(cursor.moveToPosition(0));
        assertFalse(cursor.moveToPosition(1));
        assertFalse(cursor.move(1));
        assertFalse(cursor.move(-2));
    }

    @Test
    public void testSimple1() {

        final Cursor cursor = StormCursorMock.newInstance(
                SimpleClass.class,
                new StormCursorMock.Row(1L, "1")
        );

        assertEquals(cursor.getCount(), 1);
        assertTrue(cursor.moveToFirst());
        assertTrue(cursor.moveToPosition(0));

        assertEquals(cursor.getType(cursor.getColumnIndex("id")), Cursor.FIELD_TYPE_INTEGER);
        assertEquals(cursor.getType(cursor.getColumnIndex("someString")), Cursor.FIELD_TYPE_STRING);

        assertEquals(cursor.getLong(0), 1L);
        assertEquals(cursor.getString(1), "1");
    }

    @Test
    public void testSimple2() {

        final Cursor cursor = StormCursorMock.newInstance(
                SimpleClass.class,
                new StormCursorMock.Row(12L, "12"),
                new StormCursorMock.Row(134L, null)
        );

        assertEquals(cursor.getCount(), 2);

        assertTrue(cursor.moveToFirst());
        assertEquals(cursor.getPosition(), 0);

        assertTrue(cursor.moveToPosition(0));
        assertEquals(cursor.getPosition(), 0);

        assertTrue(cursor.moveToPosition(1));
        assertEquals(cursor.getPosition(), 1);

        assertFalse(cursor.moveToPosition(2));

        assertTrue(cursor.moveToLast());
        assertEquals(cursor.getPosition(), 1);

        assertTrue(cursor.moveToFirst());
        assertTrue(cursor.moveToNext());
        assertEquals(cursor.getPosition(), 1);

        assertFalse(cursor.moveToNext());
        assertTrue(cursor.isAfterLast());

        cursor.moveToFirst();

        assertEquals(cursor.getType(cursor.getColumnIndex("id")), Cursor.FIELD_TYPE_INTEGER);
        assertEquals(cursor.getType(cursor.getColumnIndex("someString")), Cursor.FIELD_TYPE_STRING);

        assertEquals(cursor.getLong(0), 12L);
        assertEquals(cursor.getString(1), "12");

        cursor.moveToNext();

        assertEquals(cursor.getType(cursor.getColumnIndex("id")), Cursor.FIELD_TYPE_INTEGER);
        assertEquals(cursor.getType(cursor.getColumnIndex("someString")), Cursor.FIELD_TYPE_NULL);

        assertEquals(cursor.getLong(0), 134L);
    }
}
