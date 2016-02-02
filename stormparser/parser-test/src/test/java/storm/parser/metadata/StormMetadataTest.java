package storm.parser.metadata;

import android.net.Uri;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.parser.ParserAssert;
import storm.parser.StormParserException;
import storm.parser.StormParserFactory;

/**
 * Created by Dimitry Ivanov on 14.01.2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class StormMetadataTest extends TestCase {

    private <T> StormMetadata<T> getMetadata(Class<T> cl) {

        ParserAssert.assertApt(cl, StormMetadataAptClassNameBuilder.getInstance());

        try {
            return new StormParserFactory().provide(cl).metadata();
        } catch (StormParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Table
    static class TableNoName {
        @PrimaryKey
        @Column
        long id;
    }

    @Test
    public void testNoTableName() {
        assertEquals(getMetadata(TableNoName.class).tableName(), "TableNoName");
    }


    @Table("my_table_name")
    static class TableWithName {
        @PrimaryKey
        @Column
        long id;
    }

    @Test
    public void testTableWithName() {
        assertEquals(getMetadata(TableWithName.class).tableName(), "my_table_name");
    }


    @Table
    static class UriDefaultClass {
        @PrimaryKey
        @Column
        long id;
    }

    @Test
    public void testDefaultUri() {

        assertEquals(
                getMetadata(UriDefaultClass.class).notificationUri(),
                notificationUri(UriDefaultClass.class, null)
        );
    }


    @Table(notificationUri = "custom://uri")
    static class UriCustomClass {
        @PrimaryKey
        @Column
        long id;
    }

    @Test
    public void testCustomUri() {
        assertEquals(
                getMetadata(UriCustomClass.class).notificationUri(),
                notificationUri(UriCustomClass.class, "custom://uri")
        );
    }

    // we are adding parent class to the name to avoid possible conflicts
    // two inner classes might have the same name inside one package (but be in different classes)
    private static Uri notificationUri(Class<?> cl, String def) {
        return Uri.parse(
                StormNotificationUriBuilder.getDefault(
                        cl.getPackage().getName(),
                        StormMetadataTest.class.getSimpleName() + "." + cl.getSimpleName(),
                        def
                )
        );
    }

    @Table
    static class PrimaryKeyNoAutoincrement {
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
    static class PrimaryKeyAutoincrement {
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
    static class PrimaryKeySelectionTable {
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
