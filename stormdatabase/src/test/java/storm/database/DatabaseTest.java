package storm.database;

import android.database.sqlite.SQLiteDatabase;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import storm.db.Database;
import storm.db.DatabaseException;

/**
 * Created by Dimitry Ivanov on 20.12.2015.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DatabaseTest extends TestCase {

    private static Database.Configuration configuration(int version) {
        return new Database.Configuration(
                RuntimeEnvironment.application.getApplicationContext(),
                "database_test",
                version
        );
    }

    @Test
    public void testNullConfiguration() {
        try {
            new Database(null);
            assertTrue(false);
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testNoModules() {
        try {
            new Database(configuration(1)).open();
            assertTrue(false);
        } catch (DatabaseException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testClosedSqliteInsteadOf() {
        final Database database = new Database(configuration(1))
                .registerModule(new AllMethodsModule());
        final SQLiteDatabase db = database.open();
        assertTrue(db != null);
        db.close();
        try {
            database.open();
            assertTrue(false);
        } catch (DatabaseException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testModuleRegistered() {
        final AllMethodsModule allMethodsModule = new AllMethodsModule();
        final Database database = new Database(configuration(1))
                .registerModule(allMethodsModule);
        database.open();

        assertTrue(allMethodsModule.isOnCreateCalled);
        assertTrue(allMethodsModule.isOnOpenCalled);
        assertTrue(allMethodsModule.isOnConfigureCalled);
        assertFalse(allMethodsModule.isOnUpgradeCalled);
    }

    @Test
    public void testModulesRegistered() {
        final List<AllMethodsModule> allMethodsModules = Arrays.asList(
                new AllMethodsModule(),
                new AllMethodsModule(),
                new AllMethodsModule(),
                new AllMethodsModule(),
                new AllMethodsModule()
        );
        final Database database = new Database(configuration(1))
                .registerModules(allMethodsModules);
        database.open();

        for (AllMethodsModule module: allMethodsModules) {
            assertTrue(module.isOnCreateCalled);
            assertTrue(module.isOnOpenCalled);
            assertTrue(module.isOnConfigureCalled);
            assertFalse(module.isOnUpgradeCalled);
        }
    }

    @Test
    public void testOnUpgradeCalled() {

        final Database first = new Database(configuration(1))
                .registerModule(new AllMethodsModule());
        first.open();

        final AllMethodsModule module = new AllMethodsModule();
        final Database database = new Database(configuration(2))
                .registerModule(module);
        database.open();

        assertTrue(module.isOnUpgradeCalled);
        assertTrue(module.isOnOpenCalled);
        assertTrue(module.isOnConfigureCalled);
        assertFalse(module.isOnCreateCalled);
    }
}
