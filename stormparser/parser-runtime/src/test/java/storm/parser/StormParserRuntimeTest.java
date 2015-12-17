package storm.parser;

import android.content.ContentValues;
import android.database.Cursor;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Serialize;
import storm.serializer.StormSerializer;
import storm.serializer.pack.BooleanIntSerializer;
import storm.serializer.pack.BooleanStringSerializer;
import storm.serializer.pack.DateLongSerializer;

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
            // we skip checking for Class<?> equality because we are using
            // anonymous class for fast creation of this object

            Test1 test1 = (Test1) o;

            if (id != test1.id) return false;
            if (Float.compare(test1.someFloat, someFloat) != 0) return false;
            if (Double.compare(test1.someDouble, someDouble) != 0) return false;
            if (someInt != test1.someInt) return false;
            if (someString != null ? !someString.equals(test1.someString) : test1.someString != null)
                return false;
            return Arrays.equals(byteArray, test1.byteArray);

        }

        @Override
        public String toString() {
            return "Test1{" +
                    "id=" + id +
                    ", someString='" + someString + '\'' +
                    ", someFloat=" + someFloat +
                    ", someDouble=" + someDouble +
                    ", someInt=" + someInt +
                    ", byteArray=" + Arrays.toString(byteArray) +
                    '}';
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
    public void test1_1() {

        final StormParser<Test1> parser = getParser(Test1.class, null);
        final Cursor cursor = StormCursorMock.newInstance(
                Test1.class,
                new StormCursorMock.Row(2L, "someString", 3.F, -.05D, -88, new byte[]{(byte) 1})
        );

        final Test1 initial = new Test1() {{
            id = 2L; someString = "someString"; someFloat = 3.F; someDouble = -.05D; someInt = -88; byteArray = new byte[] {(byte) 1};
        }};
        final Test1 parsed = parser.fromCursor(cursor);

        assertTrue(parsed != null);
        assertTrue(parsed.equals(initial));

        final ContentValues cvWith = parser.toContentValues(initial, true);
        final ContentValues cvWithout = parser.toContentValues(initial, false);

        assertTrue(cvWith.size() == 6);
        assertTrue(cvWithout.size() == 5);

        assertContentValuesNotNull(cvWith, "id", "someString", "someFloat", "some_double", "someInt", "byteArray");
        assertContentValuesNotNull(cvWithout, "someString", "someFloat", "some_double", "someInt", "byteArray");
    }

    @Test
    public void test1_2() {

        final StormParser<Test1> parser = getParser(Test1.class, null);
        final Cursor cursor = StormCursorMock.newInstance(
                Test1.class,
                new StormCursorMock.Row(11L, null, null, null, 15, null)
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

        assertContentValuesNotNull(cvWith, "id", "someInt");
        assertContentValuesNotNull(cvWithout, "someInt");

        assertContentValuesNull(cvWith, "someString", "someFloat", "some_double", "byteArray");
        assertContentValuesNull(cvWithout, "someString", "someFloat", "some_double", "byteArray");
    }


    private static class Test2 {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            Test2 test2 = (Test2) o;

            if (someBooleanInt != test2.someBooleanInt) return false;
            if (someBooleanString != test2.someBooleanString) return false;
            if (id != null ? !id.equals(test2.id) : test2.id != null) return false;
            return !(someDate != null ? !someDate.equals(test2.someDate) : test2.someDate != null);
        }

        @Override
        public String toString() {
            return "Test2{" +
                    "id='" + id + '\'' +
                    ", someBooleanInt=" + someBooleanInt +
                    ", someBooleanString=" + someBooleanString +
                    ", someDate=" + someDate +
                    '}';
        }

        @Column
        @PrimaryKey
        String id;

        @Column
        @Serialize(BooleanIntSerializer.class)
        boolean someBooleanInt;

        @Column
        @Serialize(BooleanStringSerializer.class)
        boolean someBooleanString;

        @Column
        @Serialize(DateLongSerializer.class)
        Date someDate;
    }

    @Test
    public void test2() {
        final StormParser<Test2> parser = getParser(Test2.class, new StormSerializerProvider() {
            @Override
            public <IN, OUT> StormSerializer<IN, OUT> provide(Class<IN> cl) {
                //noinspection unchecked
                return (StormSerializer<IN, OUT>) storm.reflect.ReflectionInstanceCreator.newInstance(cl);
            }
        });

        final long time = System.currentTimeMillis();

        final Cursor cursor = StormCursorMock.newInstance(
                Test2.class,
                new StormCursorMock.Row("123", 1, "false", time)
        );

        final Test2 initial = new Test2() {{
            id = "123"; someBooleanInt = true; someBooleanString = false; someDate = new Date(time);
        }};

        final Test2 parsed = parser.fromCursor(cursor);

        assertTrue(parsed != null);
        assertTrue(parsed.equals(initial));

        final ContentValues cvWith = parser.toContentValues(initial, true);
        final ContentValues cvWithout = parser.toContentValues(initial, false);

        assertContentValuesNotNull(cvWith, "id", "someBooleanInt", "someBooleanString", "someDate");
        assertContentValuesNotNull(cvWithout, "someBooleanInt", "someBooleanString", "someDate");
    }


    private static class Test3 {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            Test3 test3 = (Test3) o;

            if (id != test3.id) return false;
            return !(someString != null ? !someString.equals(test3.someString) : test3.someString != null);

        }

        @PrimaryKey(autoincrement = true)
        @Column
        long id;

        @Column
        String someString;
    }

    @Test
    public void test3_1() {

        final StormParser<Test3> parser = getParser(Test3.class, null);

        final Cursor cursor = StormCursorMock.newInstance(
                Test3.class,
                new StormCursorMock.Row(0L, "0"),
                new StormCursorMock.Row(1L, "1"),
                new StormCursorMock.Row(2L, "2")
        );

        final List<Test3> initial = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            final long val = i;
            initial.add(new Test3() {{id = val; someString = String.valueOf(val);}});
        }

        cursor.moveToFirst();
        final List<Test3> parsed = parser.fromCursorList(cursor);

        assertTrue(initial.equals(parsed));
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

    private void assertContentValuesNull(ContentValues cv, String... nullKeys) {

        Object object;

        for (String key: nullKeys) {

            object = cv.get(key);

            if (object == null) {
                assertTrue(true);
                continue;
            }

            if (object instanceof Number) {
                assertTrue(String.format("Asserting null key `%s` in `%s`", key, cv), ((Number) object).intValue() == 0);
                continue;
            }

            assertTrue(String.format("Asserting null key `%s` in `%s`", key, cv), false);
        }
    }

    private void assertContentValuesNotNull(ContentValues cv, String... notNullKeys) {
        for (String key: notNullKeys) {
            assertTrue(String.format("Asserting not null key `%s` in `%s`", key, cv), cv.get(key) != null);
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