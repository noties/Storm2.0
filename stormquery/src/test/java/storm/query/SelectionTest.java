package storm.query;

/**
 * Created by Dimitry Ivanov on 10.10.2015.
 */
public class SelectionTest extends AbsStatementTest<Selection> {

    @Override
    protected Selection setUpStatement() {
        return new Selection();
    }

    public void testEquals() {
        builder.equals("col", "value");
        assertStatement("col = ?");
        assertArgs("value");
    }

    public void testNotEquals() {
        builder.notEquals("col", 2);
        assertStatement("col != ?");
        assertArgs("2");
    }

    public void testGreater() {
        builder.greater("col", 66);
        assertStatement("col > ?");
        assertArgs("66");
    }

    public void testLess() {
        builder.less("col", 101);
        assertStatement("col < ?");
        assertArgs("101");
    }

    public void testGreaterEquals() {
        builder.greaterEquals("col", 77);
        assertStatement("col >= ?");
        assertArgs("77");
    }

    public void testLessEquals() {
        builder.lessEquals("col", 13);
        assertStatement("col <= ?");
        assertArgs("13");
    }

    public void testIsNull() {
        builder.isNull("col");
        assertStatement("col IS NULL");
        assertArgs();
    }

    public void testIsNotNull() {
        builder.isNotNull("col");
        assertStatement("col IS NOT NULL");
        assertArgs();
    }

    public void testLike() {
        builder.like("col", "%mid%");
        assertStatement("col LIKE '%mid%'");
        assertArgs();
    }

    public void testNotLike() {
        builder.notLike("col", "%mid%");
        assertStatement("col NOT LIKE '%mid%'");
        assertArgs();
    }

    public void testGlob() {
        builder.glob("col", "?abc*");
        assertStatement("col GLOB '?abc*'");
        assertArgs();
    }

    public void testNotGlob() {
        builder.notGlob("col", "*abc?");
        assertStatement("col NOT GLOB '*abc?'");
        assertArgs();
    }

    public void testBetween() {
        builder.between("col", 12, 44);
        assertStatement("col BETWEEN ? AND ?");
        assertArgs("12", "44");
    }

    public void testNotBetween() {
        builder.notBetween("col", 33, 999);
        assertStatement("col NOT BETWEEN ? AND ?");
        assertArgs("33", "999");
    }

    public void testIn() {
        builder.in("col", 1, 3, 5, 7, 9);
        assertStatement("col IN (?, ?, ?, ?, ?)");
        assertArgs("1", "3", "5", "7", "9");
    }

    public void testNotIn() {
        builder.notIn("col", 22, 33, 44, 55);
        assertStatement("col NOT IN (?, ?, ?, ?)");
        assertArgs("22", "33", "44", "55");
    }

    public void testRaw() {
        builder.raw("col_1 = ? AND col_2 != ?", 101, 3);
        assertStatement("col_1 = ? AND col_2 != ?");
        assertArgs("101", "3");
    }

    public void testGroups() {
        builder.groupStart()
                .equals("col", 1)
                .groupEnd();
        assertStatement("(col = ?)");
        assertArgs("1");
    }

    public void testAnd() {
        builder.equals("col_1", 11)
                .and()
                .notEquals("col_2", 3);
        assertStatement("col_1 = ? AND col_2 != ?");
        assertArgs("11", "3");
    }

    public void testOr() {
        builder.equals("col_1", 12)
                .or()
                .notEquals("col_2", 19);
        assertStatement("col_1 = ? OR col_2 != ?");
        assertArgs("12", "19");
    }

    public void testComplex1() {
        builder.equals("col_1", 33)
                .and()
                .groupStart()
                    .notEquals("col_2", 11)
                    .or()
                    .notLike("col_3", "%mid%")
                .groupEnd()
                .and()
                .in("col_4", 1, 10, 100, 1000);
        assertStatement("col_1 = ? AND (col_2 != ? OR col_3 NOT LIKE '%mid%') AND col_4 IN (?, ?, ?, ?)");
        assertArgs("33", "11", "1", "10", "100", "1000");
    }

    public void testComplex2() {
        builder.in("col_1", 9, 99, 999, 9999)
                .and()
                .groupStart()
                    .isNull("col_2")
                    .and()
                    .groupStart()
                        .isNotNull("col_3")
                        .or()
                        .between("col_4", 0, Integer.MAX_VALUE)
                    .groupEnd()
                    .or()
                    .groupStart()
                        .glob("col_5", "*end")
                        .and()
                        .notGlob("col_5", "start*")
                    .groupEnd()
                .groupEnd()
                .or()
                .greater("col_6", 654)
                .and()
                .less("col_7", 12);

        assertStatement("col_1 IN (?, ?, ?, ?) AND (col_2 IS NULL AND (col_3 IS NOT NULL " +
                "OR col_4 BETWEEN ? AND ?) OR (col_5 GLOB '*end' " +
                "AND col_5 NOT GLOB 'start*')) OR col_6 > ? AND col_7 < ?");
        assertArgs("9", "99", "999", "9999", "0", String.valueOf(Integer.MAX_VALUE), "654", "12");
    }
}
