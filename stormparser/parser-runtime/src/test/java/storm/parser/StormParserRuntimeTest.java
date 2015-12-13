package storm.parser;

import android.content.ContentValues;
import android.database.Cursor;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class StormParserRuntimeTest extends TestCase {

    private static class Test1 {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Test1 test1 = (Test1) o;

            if (id != test1.id) return false;
            if (Float.compare(test1.someFloat, someFloat) != 0) return false;
            if (Double.compare(test1.someDouble, someDouble) != 0) return false;
            if (someInt != test1.someInt) return false;
            if (someString != null ? !someString.equals(test1.someString) : test1.someString != null)
                return false;
            return Arrays.equals(byteArray, test1.byteArray);

        }

        @Column
        @PrimaryKey
        long id;

        @Column
        String someString;

        @Column
        float someFloat;

        @Column("some_double")
        double someDouble;

        @Column
        int someInt;

        @Column
        byte[] byteArray;
    }

    @Test
    public void test1() {

        final StormParser<Test1> parser = getParser(Test1.class, null);
        final Cursor cursor = StormCursorMock1.newInstance(
                Test1.class,
                2L, "someString", 3.F, -.05D, -88, new byte[]{(byte) 1}
        );

        final Test1 initial = new Test1() {{
            id = 2L; someString = "someString"; someFloat = 3.F; someDouble = -.05D; someInt = -88; byteArray = new byte[] {(byte) 1};
        }};
        final Test1 parsed = parser.fromCursor(cursor);

        assertTrue(parsed != null);
        assertTrue(parsed.equals(initial));

        final ContentValues withPrimary = parser.toContentValues(initial, true);
        final ContentValues withoutPrimary = parser.toContentValues(initial, false);

        assertTrue(withPrimary.size() == 6);
        assertTrue(withoutPrimary.size() == 5);

        // todo additional testing of content values
    }

    @Test
    public void test1_2() {

        final StormParser<Test1> parser = getParser(Test1.class, null);
        final Cursor cursor = StormCursorMock1.newInstance(
                Test1.class,
                11L, null, null, null, 15, null
        );

        final Test1 initial = new Test1() {{
            id = 11L; someInt = 15;
        }};
        final Test1 parsed = parser.fromCursor(cursor);

        assertTrue(parsed != null);
        assertTrue(parsed.equals(initial));

        final ContentValues cvWith = parser.toContentValues(initial, true);
        final ContentValues cvWithout = parser.toContentValues(initial, false);

        assertTrue(cvWith.size() == 6);
        assertTrue(cvWithout.size() == 5);

        // todo additional testing of content values
    }

    static <T> StormParser<T> getParser(Class<T> cl, StormSerializerProvider serializerProvider) {
        try {
            return new StormParserProviderRuntime().provideParser(
                    cl,
                    new ReflectionInstanceCreator<T>(cl),
                    serializerProvider
            );
        } catch (StormParserException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ReflectionInstanceCreator<T> implements StormInstanceCreator<T> {

        final Class<T> mClass;

        ReflectionInstanceCreator(Class<T> cl) {
            this.mClass = cl;
        }

        @Override
        public T create() {
            return storm.reflect.ReflectionInstanceCreator.newInstance(mClass);
        }
    }
}
