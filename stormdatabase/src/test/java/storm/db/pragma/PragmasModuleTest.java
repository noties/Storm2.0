package storm.db.pragma;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import storm.db.Database;

/**
 * Created by Dimitry Ivanov on 20.12.2015.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PragmasModuleTest extends TestCase {

    @Test
    public void testPragmaStatement() {
        final Pragma pragma = AutoVacuumPragma.incremental();
        final String statement = PragmasModule.createPragmaStatement(pragma);
        assertEquals("PRAGMA auto_vacuum = \'2\';", statement);
    }

    @Test
    public void testForeignKeys() {
        assertPragma(ForeignKeysPragma.off(), "foreign_keys", "0");
        assertPragma(ForeignKeysPragma.on(), "foreign_keys", "1");
    }

    @Test
    public void testJournalMode() {
        assertPragma(JournalModePragma.delete(), "journal_mode", "DELETE");
        assertPragma(JournalModePragma.truncate(), "journal_mode", "TRUNCATE");
        assertPragma(JournalModePragma.persist(), "journal_mode", "PERSIST");
        assertPragma(JournalModePragma.memory(), "journal_mode", "MEMORY");
        assertPragma(JournalModePragma.wal(), "journal_mode", "WAL");
        assertPragma(JournalModePragma.off(), "journal_mode", "OFF");
    }

    @Test
    public void testSynchronous() {
        assertPragma(SynchronousPragma.off(), "synchronous", "0");
        assertPragma(SynchronousPragma.normal(), "synchronous", "1");
        assertPragma(SynchronousPragma.full(), "synchronous", "2");
    }

    @Test
    public void testTempStore() {
        assertPragma(TempStorePragma.def(), "temp_store", "0");
        assertPragma(TempStorePragma.file(), "temp_store", "1");
        assertPragma(TempStorePragma.memory(), "temp_store", "2");
    }

    @Test
    public void testLockingMode() {
        assertPragma(LockingModePragma.normal(), "locking_mode", "NORMAL");
        assertPragma(LockingModePragma.exclusive(), "locking_mode", "EXCLUSIVE");
    }

    @Test
    public void testAutoVacuum() {
        assertPragma(AutoVacuumPragma.off(), "auto_vacuum", "0");
        assertPragma(AutoVacuumPragma.full(), "auto_vacuum", "1");
        assertPragma(AutoVacuumPragma.incremental(), "auto_vacuum", "2");
    }

    private static void assertPragma(Pragma pragma, String name, String value) {
        assertEquals(pragma.getName(), name);
        assertEquals(pragma.getValue(), value);
    }


    @Test
    public void testPragmaApplied() {

        final Database db = new Database(new Database.Configuration(
                RuntimeEnvironment.application,
                "pragmas_test", 1
        ));

        // Robolectric fails for `auto_vacuum` & `journal_mode`
        final Pragma[] pragmas = new Pragma[] {
                AutoVacuumPragma.incremental(),
                ForeignKeysPragma.on(),
                JournalModePragma.memory(),
                LockingModePragma.exclusive(),
                SynchronousPragma.off(),
                TempStorePragma.memory()
        };

        db.registerModule(PragmasModule.newInstance(pragmas));

        final SQLiteDatabase database = db.open();

        Cursor cursor;

        for (Pragma pragma: pragmas) {

            cursor = database.rawQuery("PRAGMA " + pragma.getName(), null);
            if (cursor != null
                    && cursor.moveToFirst()) {
                try {
                    assertEquals("Asserting pragma `" + pragma.getName() + "`", pragma.getValue().toLowerCase(), cursor.getString(0).toLowerCase());
                } finally {
                    cursor.close();
                }
            } else {
                throw new IllegalStateException("Returned cursor has not data/invalid");
            }
        }
    }
}
