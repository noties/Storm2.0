package storm.parser;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import storm.annotations.Column;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class StormParserProviderRuntimeTest extends TestCase {

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


    private static class NormalClass {
        @Column
        long someLong;
    }

    @Test
    public void testNormal() {
        try {
            provide(NormalClass.class);
            assertTrue(true);
        } catch (StormParserException e) {
            assertTrue(false);
        }
    }

    static <T> StormParser<T> provide(Class<T> cl) throws StormParserException {
        return new StormParserProviderRuntime().provideParser(cl, null, null);
    }
}
