package storm.types;

import junit.framework.TestCase;

import java.lang.reflect.Field;

/**
 * Created by Dimitry Ivanov on 12.12.2015.
 */
public class StormTypesTest extends TestCase {

    public void testParseTypeInteger() {
        assertParseType(StormType.INT, ParseTypeInteger.class);
    }

    public void testParseTypeLong() {
        assertParseType(StormType.LONG, ParseTypeLong.class);
    }

    public void testParseTypeFloat() {
        assertParseType(StormType.FLOAT, ParseTypeFloat.class);
    }

    public void testParseTypeDouble() {
        assertParseType(StormType.DOUBLE, ParseTypeDouble.class);
    }

    public void testParseTypeString() {
        assertParseType(StormType.STRING, ParseTypeString.class);
    }

    public void testParseTypeByteArray() {
        assertParseType(StormType.BYTE_ARRAY, ParseTypeByteArray.class);
    }

    private void assertParseType(StormType expected, Class<?> cl) {
        final Field[] fields = cl.getDeclaredFields();
        if (fields == null
                || fields.length == 0) {
            assertTrue(false);
            return;
        }
        for (Field f: fields) {
            assertEquals(expected, StormType.forValue(f.getType()));
        }
    }


    private static class ParseTypeInteger {
        int somePrimitive;
        Integer someBoxed;
    }

    private static class ParseTypeLong {
        long somePrimitive;
        Long someBoxed;
    }

    private static class ParseTypeFloat {
        float somePrimitive;
        Float someBoxed;
    }

    private static class ParseTypeDouble {
        double somePrimitive;
        Double someBoxed;
    }

    private static class ParseTypeString {
        String someString;
    }

    private static class ParseTypeByteArray {
        byte[] byteArray;
    }
}
