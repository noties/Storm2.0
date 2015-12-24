package storm.scheme;

import junit.framework.TestCase;

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
 * Created by Dimitry Ivanov on 10.12.2015.
 */
public class SchemeParserRuntimeTest extends TestCase {

    public void testTableNoAnnotation() {
        try {
            new StormSchemeProviderRuntime().provide(TableNoAnnotation.class);
            assertTrue(false);
        } catch (StormSchemeException e) {
            assertTrue(true);
        }
    }

    public void testTableNoFields() {
        try {
            new StormSchemeProviderRuntime().provide(TableNoFields.class);
            assertTrue(false);
        } catch (StormSchemeException e) {
            assertTrue(true);
        }
    }

    public void testTableNoPrimaryKey() {
        try {
            new StormSchemeProviderRuntime().provide(TableWithoutPrimaryKey.class);
            assertTrue(false);
        } catch (StormSchemeException e) {
            assertTrue(true);
        }
    }

    public void testPrimaryKeyNoColumn() {
        try {
            new StormSchemeProviderRuntime().provide(TablePrimaryKeyNoColumn.class);
            assertTrue(false);
        } catch (StormSchemeException e) {
            assertTrue(true);
        }
    }

    public void testTableName() {
        try {
            assertEquals(StormSchemeProviderRuntime.getTableName(TableWithName.class), "some_table_name");
        } catch (StormSchemeException e) {
            assertTrue(false);
        }
    }

    public void testIgnoreTransient() {
        try {
            final Field field = ClassWithTransientField.class.getDeclaredField("someField");
            assertFalse(StormSchemeProviderRuntime.isFieldShouldBeParsed(field));
        } catch (NoSuchFieldException e) {
            assertTrue(false);
        }
    }

    public void testParseTypeSerializedBoolNoSerializer() {

        final Class<?> cl = ParseTypeBooleanWrong.class;
        final Field[] fields = cl.getDeclaredFields();

        for (Field f: fields) {
            try {
                StormSchemeProviderRuntime.parseType(f);
                assertTrue(false);
            } catch (StormSchemeException e) {
                assertTrue(true);
            }
        }
    }

    public void testWrongSerializer() {
        try {
            final Field f = ParseTypeWrongSerializer.class.getDeclaredField("someLong");
            try {
                StormSchemeProviderRuntime.parseType(f);
                assertTrue(false);
            } catch (StormSchemeException e) {
                assertTrue(true);
            }
        } catch (NoSuchFieldException e) {
            // ignore
        }
    }

    public void testParseTypeSerializedBool() {
        assertSerialization(StormType.INT, ParseTypeBoolean.class);
    }

    public void testParseTypeSerializationInt() {
        assertSerialization(StormType.INT, ParseTypeInt.class);
    }

    public void testParseTypeSerializationLong() {
        assertSerialization(StormType.LONG, ParseTypeLong.class);
    }

    public void testParseTypeSerializationFloat() {
        assertSerialization(StormType.FLOAT, ParseTypeFloat.class);
    }

    public void testParseTypeSerializationDouble() {
        assertSerialization(StormType.DOUBLE, ParseTypeDouble.class);
    }

    public void testParseTypeSerializationString() {
        assertSerialization(StormType.STRING, ParseTypeString.class);
    }

    private void assertSerialization(StormType expected, Class<?> cl) {

        final Field[] fields = cl.getDeclaredFields();
        for (Field f: fields) {
            try {
                assertEquals(expected, StormSchemeProviderRuntime.parseType(f));
            } catch (StormSchemeException e) {
                assertTrue(false);
            }
        }
    }

    public void testNormal() {
        try {
            new StormSchemeProviderRuntime().provide(TableNormal.class);
            assertTrue(true);
        } catch (StormSchemeException e) {
            assertTrue(false);
        }
    }

    private static class TableNoAnnotation {}

    // we will use `pseudotable` to skip annotation processing for this
    @Table(isPseudoTable = true)
    private static class TableNoFields {}

    @Table(isPseudoTable = true)
    private static class TableWithoutPrimaryKey {

        @Column
        int someInt;
    }

    @Table(isPseudoTable = true)
    private static class TablePrimaryKeyNoColumn {
        @PrimaryKey
        long id;
    }

    @Table(value = "some_table_name", isPseudoTable = true)
    private static class TableWithName {
        @PrimaryKey
        long id;
    }

    private static class ClassWithTransientField {
        transient int someField;
    }

    private static class ParseTypeBooleanWrong {
        boolean somePrimitive;
        Boolean someBoxed;
    }

    private static abstract class NotSupportedSerializer implements StormSerializer<Long, Void> {}

    private static class ParseTypeWrongSerializer {
        @Serialize(NotSupportedSerializer.class)
        long someLong;
    }

    private static class ParseTypeBoolean {
        @Serialize(BooleanIntSerializer.class)
        boolean somePrimitive;

        @Serialize(BooleanIntSerializer.class)
        Boolean someBoxed;
    }

    private abstract static class VoidIntSerializer implements StormSerializer<Void, Integer> {}
    private static class ParseTypeInt {
        @Serialize(VoidIntSerializer.class)
        Void v;
    }

    private static class ParseTypeLong {
        @Serialize(DateLongSerializer.class)
        Date date;
    }

    private abstract static class VoidFloatSerializer implements StormSerializer<Void, Float> {}
    private static class ParseTypeFloat {
        @Serialize(VoidFloatSerializer.class)
        Void v;
    }

    private abstract static class VoidDoubleSerializer implements StormSerializer<Void, Double> {}
    private static class ParseTypeDouble {
        @Serialize(VoidDoubleSerializer.class)
        Void v;
    }

    private abstract static class VoidStringSerializer implements StormSerializer<Void, String> {}
    private static class ParseTypeString {
        @Serialize(VoidStringSerializer.class)
        Void v;
    }

    @Table(isPseudoTable = true)
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
}
