package storm.parser.scheme;

import junit.framework.TestCase;

import java.util.Date;
import java.util.List;

import storm.annotations.Column;
import storm.annotations.Default;
import storm.annotations.ForeignKey;
import storm.annotations.Index;
import storm.annotations.NewColumn;
import storm.annotations.NewTable;
import storm.annotations.PrimaryKey;
import storm.annotations.SQLiteNotNull;
import storm.annotations.Serialize;
import storm.annotations.Table;
import storm.annotations.Unique;
import storm.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 22.12.2015.
 */
public abstract class SchemeGeneratorBaseTest extends TestCase {

    public abstract StormScheme getScheme(Class<?> cl);

    @Table
    static class Test1 {
        @Column
        @PrimaryKey(autoincrement = true)
        long id;
    }

    public void test1() {
        assertSchemeOnCreate(Test1.class, "CREATE TABLE Test1(id INTEGER PRIMARY KEY AUTOINCREMENT);");
        assertSchemeOnUpdate(Test1.class);
    }


    @Table("custom_name")
    static class Test2 {

        @Column
        @PrimaryKey
        String key;

        @Column("custom_some_int")
        @Unique
        int someInt;
    }

    public void test2() {
        assertSchemeOnCreate(
                Test2.class,
                "CREATE TABLE custom_name(key TEXT PRIMARY KEY, custom_some_int INTEGER UNIQUE);"
        );
        assertSchemeOnUpdate(Test2.class);
    }

    @Table
    static class Test3 {

        @Column
        @PrimaryKey
        @Index(value = "hello_there_index_on_primary_key", sorting = Index.Sorting.DESC)
        double key;

        @Column("name")
        @SQLiteNotNull
        @Unique
        @Default("-1")
        long kindOfName;
    }

    public void test3() {
        assertSchemeOnCreate(
                Test3.class,
                "CREATE TABLE Test3(key REAL PRIMARY KEY, name INTEGER NOT NULL DEFAULT -1 UNIQUE);",
                "CREATE INDEX hello_there_index_on_primary_key ON Test3(key DESC);"
        );
        assertSchemeOnUpdate(Test3.class);
    }


    @Table("fourth")
    static class Test4 {

        private static abstract class DateDoubleSerializer implements StormSerializer<Date, Double> {}

        @Column("col1")
        @PrimaryKey(autoincrement = true)
        long _id;

        @Column("monster_one")
        @SQLiteNotNull
        @Unique
        @Default("0.0")
        @ForeignKey(parentTable = "fake_parent", parentColumnName = "fake_column", onUpdate = ForeignKey.ForeignKeyAction.CASCADE)
        @NewColumn(2)
        @Serialize(DateDoubleSerializer.class)
        Date someDouble;
    }

    public void test4() {
        final String secondColumn = "monster_one REAL NOT NULL DEFAULT 0.0 UNIQUE REFERENCES fake_parent(fake_column) ON UPDATE CASCADE";
        assertSchemeOnCreate(
                Test4.class,
                "CREATE TABLE fourth(col1 INTEGER PRIMARY KEY AUTOINCREMENT, " + secondColumn + ");"
        );
        assertSchemeOnUpdate(
                Test4.class,
                0, 2,
                "ALTER TABLE fourth ADD COLUMN " + secondColumn + ";"
        );
    }

    @Table
    static class Test5 {
        @Column
        @PrimaryKey
        long id;

        @Column
        int someInt;

        @Column
        long someLong;

        @Column
        float someFloat;

        @Column
        double someDouble;

        @Column
        String someString;
    }

    public void test5() {
        assertSchemeOnCreate(
                Test5.class,
                "CREATE TABLE Test5(id INTEGER PRIMARY KEY, someInt INTEGER, someLong INTEGER, someFloat REAL, someDouble REAL, someString TEXT);"
        );
        assertSchemeOnUpdate(Test5.class);
    }

    @Table("name")
    @NewTable(2)
    static class Test6 {
        @Column
        @PrimaryKey
        long id;

        @Column
        @Default("0")
        int someInt;

        @Column("some_double")
        @NewColumn(3)
        @Default("0.0")
        double someDouble;
    }

    public void test6() {
        assertSchemeOnCreate(
                Test6.class,
                "CREATE TABLE name(id INTEGER PRIMARY KEY, someInt INTEGER DEFAULT 0, some_double REAL DEFAULT 0.0);"
        );
        assertSchemeOnUpdate(
                Test6.class,
                0,
                2,
                "CREATE TABLE name(id INTEGER PRIMARY KEY, someInt INTEGER DEFAULT 0);"
        );
        assertSchemeOnUpdate(
                Test6.class,
                2,
                3,
                "ALTER TABLE name ADD COLUMN some_double REAL DEFAULT 0.0;"
        );
    }

    @Table(recreateOnUpgrade = true)
    private static class RecreateOnUpgradeColumn {
        @Column
        @PrimaryKey
        String someString;

        @Column
        long someLong;

        @NewColumn(2)
        @Column
        int someNew;
    }

    public void test7() {
        assertSchemeOnUpdate(
                RecreateOnUpgradeColumn.class,
                0,
                2,
                "DROP TABLE RecreateOnUpgradeColumn IF EXISTS;",
                "CREATE TABLE RecreateOnUpgradeColumn(someString TEXT PRIMARY KEY, someLong INTEGER, someNew INTEGER);"
        );
    }

    @Table(recreateOnUpgrade = true)
    private static class RecreateOnUpgradeTable {
        @Column
        @PrimaryKey
        int someInt;
    }

    public void test8() {
        assertSchemeOnUpdate(
                RecreateOnUpgradeTable.class,
                0,
                2,
                "DROP TABLE RecreateOnUpgradeTable IF EXISTS;",
                "CREATE TABLE RecreateOnUpgradeTable(someInt INTEGER PRIMARY KEY);"
        );
    }

    private void assertSchemeOnCreate(Class<?> cl, String... createStatements) {

        final StormScheme scheme = getScheme(cl);

        try {

            final List<String> onCreate = scheme.onCreate();

            final int size = onCreate != null ? onCreate.size() : 0;

            assertEquals(size, createStatements.length);

            if (size == 0) {
                return;
            }

            for (int i = 0; i < size; i++) {
                assertEquals(onCreate.get(i), createStatements[i]);
            }

        } catch (StormSchemeException e) {
            assertTrue(false);
        }
    }

    private void assertSchemeOnUpdate(Class<?> cl, String... onUpdate) {
        assertSchemeOnUpdate(cl, 0, Integer.MAX_VALUE, onUpdate);
    }

    private void assertSchemeOnUpdate(Class<?> cl, int oldVersion, int newVersion, String... onUpdateStatements) {

        final StormScheme scheme = getScheme(cl);

        try {
            final List<String> onUpdate = scheme.onUpgrade(oldVersion, newVersion);
            final int size = onUpdate != null ? onUpdate.size() : 0;
            assertEquals(size, onUpdateStatements.length);
            if (size == 0) {
                return;
            }

            for (int i = 0; i < size; i++) {
                assertEquals(onUpdate.get(i), onUpdateStatements[i]);
            }
        } catch (StormSchemeException e) {
            assertTrue(false);
        }
    }
}
