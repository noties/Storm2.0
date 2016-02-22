package storm.query;

/**
 * Created by Dimitry Ivanov on 10.10.2015.
 */
public class QueryTest extends AbsStatementTest<Query> {

    @Override
    protected Query setUpStatement() {
        return new Query();
    }

    public void testSimple() {

        builder
                .select()
                .from("table");

        assertStatement("SELECT * FROM table");
        assertArgs();
    }

    public void testColumns() {

        builder
                .select("col_1", "col_2")
                .from("table");

        assertStatement("SELECT col_1, col_2 FROM table");
        assertArgs();
    }

    public void testOffset() {

        builder.select()
                .from("table")
                .limit(12L)
                .offset(20L);

        assertStatement("SELECT * FROM table LIMIT ? OFFSET ?");
        assertArgs("12", "20");
    }

    public void testSubQuery() {

        builder
                .select()
                .from()
                .groupStart()
                    .select()
                    .from("table")
                .groupEnd();

        assertStatement("SELECT * FROM (SELECT * FROM table)");
        assertArgs();
    }

    public void testAllWithoutJoin() {

        builder
                .select("col_1")
                .from()
                .groupStart()
                    .select()
                    .from("table")
                .groupEnd()
                .where("id = ?", 5)
                .groupBy("name", "count(name) > ?", 2)
                .orderBy("name", Sorting.DESC)
                .limit(15);

        assertStatement("SELECT col_1 FROM (SELECT * FROM table) WHERE id = ? GROUP BY name HAVING count(name) > ? ORDER BY name DESC LIMIT ?");
        assertArgs("5", "2", "15");
    }

    public void testAll() {

        builder
                .select()
                .from()
                .groupStart()
                    .select("col_1")
                    .from("table")
                .groupEnd()
                .join("join_table_using", "id")
                .join("join_table_on", new Selection().equals("id", 13))
                .join()
                .groupStart()
                    .select()
                    .from("table_join_subquery")
                .groupEnd()
                .where("id > ?", 0)
                .groupBy("name", "count(name) = ?", 33)
                .orderBy(OrderBy.from("order_by_1", Sorting.DESC), OrderBy.from("order_by_2", Sorting.ASC))
                .limit(111);

        assertStatement("SELECT * FROM (SELECT col_1 FROM table) " +
                "JOIN join_table_using USING (id) JOIN join_table_on ON id = ? " +
                "JOIN (SELECT * FROM table_join_subquery) " +
                "WHERE id > ? GROUP BY name HAVING count(name) = ? " +
                "ORDER BY order_by_1 DESC, order_by_2 ASC LIMIT ?");
        assertArgs("13", "0", "33", "111");
    }

    public void testWhereSubQuery() {

        builder
                .select()
                .from("table")
                .where("id IN")
                .groupStart()
                    .select("id")
                    .from("table")
                .groupEnd();

        assertStatement("SELECT * FROM table WHERE id IN (SELECT id FROM table)");
        assertArgs();
    }

    public void testWhereSelection() {

        builder
                .select()
                .from("table")
                .where(new Selection().equals("id", 33));

        assertStatement("SELECT * FROM table WHERE id = ?");
        assertArgs("33");
    }

    public void testRaw() {

        builder.raw("SELECT DISTINCT * FROM (SELECT * FROM table) WHERE col > ?", 11);

        assertStatement("SELECT DISTINCT * FROM (SELECT * FROM table) WHERE col > ?");
        assertArgs("11");
    }
}
