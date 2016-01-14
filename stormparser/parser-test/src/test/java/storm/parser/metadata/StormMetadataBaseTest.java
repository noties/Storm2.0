package storm.parser.metadata;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;

/**
 * Created by Dimitry Ivanov on 14.01.2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public abstract class StormMetadataBaseTest extends TestCase {

    abstract <T> StormMetadata<T> getMetadata(Class<T> cl);

    @Table
    private static class TableNoName {
        @Column long id;
    }

    @Test
    public void testNoTableName() {
        assertEquals(getMetadata(TableNoName.class).tableName(), "TableNoName");
    }


    @Table("my_table_name")
    private static class TableWithName {
        @Column long id;
    }

    @Test
    public void testTableWithName() {
        assertEquals(getMetadata(TableWithName.class).tableName(), "my_table_name");
    }


    @Table
    private static class UriDefaultClass {
        @Column long id;
    }

    @Test
    public void testDefaultUri() {
        assertEquals(
                getMetadata(UriDefaultClass.class).notificationUri(),
                StormNotificationUriBuilder.getDefault(UriDefaultClass.class, null)
        );
    }


    @Table(notificationUri = "custom://uri")
    private static class UriCustomClass {
        @Column long id;
    }

    @Test
    public void testCustomUri() {
        assertEquals(
                getMetadata(UriCustomClass.class).notificationUri(),
                StormNotificationUriBuilder.getDefault(UriCustomClass.class, "custom://uri")
        );
    }

    @Table
    private static class PrimaryKeyNoAutoincrement {
        @PrimaryKey
        @Column
        long id;
    }

    @Test
    public void testPrimaryKeyNoAutoincrement() {
        assertEquals(
                getMetadata(PrimaryKeyNoAutoincrement.class).isPrimaryKeyAutoincrement(),
                false
        );
    }


    @Table
    private static class PrimaryKeyAutoincrement {
        @PrimaryKey(autoincrement = true)
        @Column
        long id;
    }

    @Test
    public void testPrimaryKeyAutoincrement() {
        assertEquals(
                getMetadata(PrimaryKeyAutoincrement.class).isPrimaryKeyAutoincrement(),
                true
        );
    }


    @Table
    private static class PrimaryKeySelectionTable {
        @PrimaryKey
        @Column
        long id;

        PrimaryKeySelectionTable id(long id) { this.id = id; return this; }
    }

    @Test
    public void testPrimaryKeySelection() {

        final StormMetadata<PrimaryKeySelectionTable> metadata = getMetadata(PrimaryKeySelectionTable.class);

        final long[] ids = { 56L, 120L, -90 };

        for (long id: ids) {
            assertEquals(
                    metadata.primaryKeySelection(new PrimaryKeySelectionTable().id(id)),
                    new PrimaryKeySelection("id", id)
            );
        }
    }
}
