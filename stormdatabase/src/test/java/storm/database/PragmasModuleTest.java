package storm.database;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import storm.db.pragma.ForeignKeysPragma;
import storm.db.pragma.Pragma;

/**
 * Created by Dimitry Ivanov on 20.12.2015.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PragmasModuleTest extends TestCase {

    @Test
    public void testForeignKeys() {

        final Pragma on = ForeignKeysPragma.of(true);
        final Pragma off = ForeignKeysPragma.of(false);

        assertEquals("foreign_keys", on.getName());
        assertEquals("foreign_keys", off.getName());

        assertEquals("1", on.getValue());
        assertEquals("0", off.getValue());
    }

    @Test
    public void testJournalMode() {

    }

    @Test
    public void testSynchronous() {

    }

    @Test
    public void testTempStore() {

    }
}
