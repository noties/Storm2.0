package storm.scheme;

import junit.framework.TestCase;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;

/**
 * Created by Dimitry Ivanov on 10.12.2015.
 */
public class SchemeTest extends TestCase {

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

    public void testNormal() {
        try {
            new StormSchemeProviderRuntime().provide(TableNormal.class);
            assertTrue(true);
        } catch (StormSchemeException e) {
            assertTrue(false);
        }
    }

    public void testTableName() {
        try {
            assertEquals(StormSchemeProviderRuntime.getTableName(TableWithName.class), "some_table_name");
        } catch (StormSchemeException e) {
            assertTrue(false);
        }
    }

    private static class TableNoAnnotation {}

    @Table
    private static class TableNoFields {}

    @Table
    private static class TableWithoutPrimaryKey {

        @Column
        int someInt;
    }

    @Table
    private static class TableNormal {
        @PrimaryKey
        long id;
    }

    @Table("some_table_name")
    private static class TableWithName {
        @PrimaryKey
        long id;
    }
}
