package storm.parser;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.Date;

import storm.annotations.Column;
import storm.annotations.Default;
import storm.annotations.ForeignKey;
import storm.annotations.Index;
import storm.annotations.NewColumn;
import storm.annotations.PrimaryKey;
import storm.annotations.SQLiteNotNull;
import storm.annotations.Serialize;
import storm.annotations.Table;
import storm.annotations.Unique;
import storm.serializer.StormSerializer;
import storm.serializer.pack.BooleanIntSerializer;
import storm.serializer.pack.DateLongSerializer;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class StormTableParserBasicRuntimeTest extends TestCase {

    private static final StormParserTableParser PARSER = new StormParserTableParser();
    private static final StormParserHelperRuntime RUNTIME = new StormParserHelperRuntime();

    private static class NoAnnotation {}

    @Test
    public void testNoAnnotation() {
        try {
            provide(NoAnnotation.class);
            assertTrue(false);
        } catch (StormParserException e) {
            assertTrue(true);
        }
    }


    @Table
    private static class NoFields {}

    @Test
    public void testNoFields() {
        try {
            provide(NoFields.class);
            assertTrue(false);
        } catch (StormParserException e) {
            assertTrue(true);
        }
    }


    @Table
    private static class NoSuitableFields {

        @Column
        transient int someInt;

        transient long someLong;

        String notAnnotated;
    }

    @Test
    public void testNoSuitableFields() {
        try {
            provide(NoSuitableFields.class);
            assertTrue(false);
        } catch (StormParserException e) {
            assertTrue(true);
        }
    }


    @Table
    private static class BadStormTypeClass {
        @Column
        Date mDate;
    }

    @Test
    public void testBadStormTypeClass() {
        try {
            provide(BadStormTypeClass.class);
            assertTrue(false);
        } catch (StormParserException e) {
            assertTrue(true);
        }
    }


    @Table
    private static class SimpleNormalClass {
        @Column
        long someLong;
    }

    @Test
    public void testSimpleNormal() {
        try {
            provide(SimpleNormalClass.class);
            assertTrue(true);
        } catch (StormParserException e) {
            assertTrue(false);
        }
    }


    @Table
    private static class TablePrimaryKeyNoColumn {
        @PrimaryKey
        long id;
    }

    @Test
    public void testPrimaryKeyNoColumn() {
        try {
            provide(TablePrimaryKeyNoColumn.class);
            assertTrue(false);
        } catch (StormParserException e) {
            assertTrue(true);
        }
    }

    @Table(value = "some_table_name", generateScheme = false)
    private static class TableWithName {
        @PrimaryKey
        @Column
        long id;
    }

    @Test
    public void testTableName() {
        try {
            StormParserTable table = PARSER.parseTable(RUNTIME, TableWithName.class);
            assertEquals(table.getTableName(), "some_table_name");
        } catch (StormParserException e) {
            assertTrue(false);
        }
    }


    private static class ClassWithTransientField {
        transient int someField;
    }

    @Test
    public void testIgnoreTransient() {
        try {
            final Field field = ClassWithTransientField.class.getDeclaredField("someField");
            assertFalse(RUNTIME.shouldParseElement(field));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }


    private static class ParseTypeBooleanWrong {
        boolean somePrimitive;
        Boolean someBoxed;
    }

    @Test
    public void testParseTypeSerializedBoolNoSerializer() {

        final Class<?> cl = ParseTypeBooleanWrong.class;
        final Field[] fields = cl.getDeclaredFields();

        for (Field f: fields) {
            try {
                PARSER.parseType(RUNTIME, f.getDeclaringClass(), f);
            } catch (StormParserException e) {
                assertTrue(true);
            }
        }
    }


    private static abstract class NotSupportedSerializer implements StormSerializer<Long, Void> {}
    private static class ParseTypeWrongSerializer {
        @Serialize(NotSupportedSerializer.class)
        long someLong;
    }


    @Test
    public void testWrongSerializer() {
        try {
            final Field f = ParseTypeWrongSerializer.class.getDeclaredField("someLong");
            try {
                PARSER.parseType(RUNTIME, f.getDeclaringClass(), f);
                assertTrue(false);
            } catch (StormParserException e) {
                assertTrue(true);
            }
        } catch (NoSuchFieldException e) {
            // ignore
        }
    }

    private static class ParseTypeBoolean {
        @Serialize(BooleanIntSerializer.class)
        boolean somePrimitive;

        @Serialize(BooleanIntSerializer.class)
        Boolean someBoxed;
    }

    public void testParseTypeSerializedBool() {
        assertSerialization(StormType.INT, ParseTypeBoolean.class);
    }


    private abstract static class VoidIntSerializer implements StormSerializer<Void, Integer> {}
    private static class ParseTypeInt {
        @Serialize(VoidIntSerializer.class)
        Void v;
    }

    public void testParseTypeSerializationInt() {
        assertSerialization(StormType.INT, ParseTypeInt.class);
    }


    private static class ParseTypeLong {
        @Serialize(DateLongSerializer.class)
        Date date;
    }

    public void testParseTypeSerializationLong() {
        assertSerialization(StormType.LONG, ParseTypeLong.class);
    }


    private abstract static class VoidFloatSerializer implements StormSerializer<Void, Float> {}
    private static class ParseTypeFloat {
        @Serialize(VoidFloatSerializer.class)
        Void v;
    }

    public void testParseTypeSerializationFloat() {
        assertSerialization(StormType.FLOAT, ParseTypeFloat.class);
    }

    private abstract static class VoidDoubleSerializer implements StormSerializer<Void, Double> {}
    private static class ParseTypeDouble {
        @Serialize(VoidDoubleSerializer.class)
        Void v;
    }

    public void testParseTypeSerializationDouble() {
        assertSerialization(StormType.DOUBLE, ParseTypeDouble.class);
    }

    private abstract static class VoidStringSerializer implements StormSerializer<Void, String> {}
    private static class ParseTypeString {
        @Serialize(VoidStringSerializer.class)
        Void v;
    }

    public void testParseTypeSerializationString() {
        assertSerialization(StormType.STRING, ParseTypeString.class);
    }

    @Table(generateScheme = false)
    private static class TableNormal {

        @Column
        @PrimaryKey(autoincrement = true)
        long id;

        @Column
        String str;

        @Column
        @Index("some_column_with_index")
        int someInt;

        @Column
        @Unique
        float someFloat;

        @Column
        @Default("0.01")
        double someDouble;

        @Column
        @SQLiteNotNull
        @Serialize(BooleanIntSerializer.class)
        boolean someBoolean;

        @Column
        @ForeignKey(parentTable = "parent_table", parentColumnName = "parent_column", onDelete = ForeignKey.ForeignKeyAction.CASCADE)
        int againInt;

        @Column
        @NewColumn(2)
        long someAddedAtVersion2;
    }

    @Test
    public void testNormalClass() {
        try {
            provide(SimpleNormalClass.class);
            assertTrue(true);
        } catch (StormParserException e) {
            assertTrue(false);
        }
    }

    static StormParserTable provide(Class<?> cl) throws StormParserException {
        return PARSER.parseTable(RUNTIME, cl);
    }

    void assertSerialization(StormType expected, Class<?> cl) {

        final Field[] fields = cl.getDeclaredFields();
        for (Field f: fields) {
            try {
                assertEquals(expected, PARSER.parseType(RUNTIME, f.getDeclaringClass(), f));
            } catch (StormParserException e) {
                assertTrue(false);
            }
        }
    }
}
