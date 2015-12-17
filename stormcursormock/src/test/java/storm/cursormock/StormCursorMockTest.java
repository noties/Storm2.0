package storm.cursormock;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(manifest = Config.NONE)
public class StormCursorMockTest extends TestCase {

    private static class EmptyClass {}

    @Test
    public void testEmptyClass() {
        try {
            StormCursorMock.newInstance(EmptyClass.class);
            assertTrue(false);
        } catch (IllegalStateException e) {
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
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }


}
