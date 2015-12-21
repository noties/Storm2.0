package storm.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dimitry Ivanov on 10.10.2015.
 */
public class Selection implements IStatementBuilder {

    public static Selection eq(String col, Object arg) {
        return new Selection().equals(col, arg);
    }

//    public static Selection custom(String statement, Object... args) {
//        return new Selection().raw(statement, args);
//    }

    private final StringBuilder mBuilder;
    private final List<String> mArgs;

    public Selection() {
        this.mBuilder = new StringBuilder();
        this.mArgs = new ArrayList<>();
    }

    public Selection equals(String col, Object arg) {
        mBuilder.append(col)
                .append(" = ?");
        mArgs.add(str(arg));
        return this;
    }

    public Selection notEquals(String col, Object arg) {
        mBuilder.append(col)
                .append(" != ?");
        mArgs.add(str(arg));
        return this;
    }

    public Selection greater(String col, Number arg) {
        mBuilder.append(col)
                .append(" > ?");
        mArgs.add(str(arg));
        return this;
    }

    public Selection less(String col, Number arg) {
        mBuilder.append(col)
                .append(" < ?");
        mArgs.add(str(arg));
        return this;
    }

    public Selection greaterEquals(String col, Number arg) {
        mBuilder.append(col)
                .append(" >= ?");
        mArgs.add(str(arg));
        return this;
    }

    public Selection lessEquals(String col, Number arg) {
        mBuilder.append(col)
                .append(" <= ?");
        mArgs.add(str(arg));
        return this;
    }

    public Selection isNull(String col) {
        mBuilder.append(col)
                .append(" IS NULL");
        return this;
    }

    public Selection isNotNull(String col) {
        mBuilder.append(col)
                .append(" IS NOT NULL");
        return this;
    }

    public Selection like(String col, String pattern) {
        mBuilder.append(col)
                .append(" LIKE '")
                .append(pattern)
                .append("'");
        return this;
    }

    public Selection notLike(String col, String pattern) {
        mBuilder.append(col)
                .append(" NOT LIKE '")
                .append(pattern)
                .append("'");
        return this;
    }

    public Selection glob(String col, String pattern) {
        mBuilder.append(col)
                .append(" GLOB '")
                .append(pattern)
                .append("'");
        return this;
    }

    public Selection notGlob(String col, String pattern) {
        mBuilder.append(col)
                .append(" NOT GLOB '")
                .append(pattern)
                .append("'");
        return this;
    }

    public Selection between(String col, Number from, Number to) {
        mBuilder.append(col)
                .append(" BETWEEN ? AND ?");
        mArgs.add(str(from));
        mArgs.add(str(to));
        return this;
    }

    public Selection notBetween(String col, Number from, Number to) {
        mBuilder.append(col)
                .append(" NOT BETWEEN ? AND ?");
        mArgs.add(str(from));
        mArgs.add(str(to));
        return this;
    }

    public Selection in(String col, Object... args) {
        mBuilder.append(col)
                .append(" IN (");
        boolean first = true;
        for (Object o: args) {
            if (!first) {
                mBuilder.append(", ");
            } else {
                first = false;
            }
            mBuilder.append('?');
            this.mArgs.add(str(o));
        }
        mBuilder.append(")");
        return this;
    }

    public Selection notIn(String col, Object...args) {
        mBuilder.append(col)
                .append(" NOT IN (");
        boolean first = true;
        for (Object o: args) {
            if (!first) {
                mBuilder.append(", ");
            } else {
                first = false;
            }
            mBuilder.append('?');
            this.mArgs.add(str(o));
        }
        mBuilder.append(")");
        return this;
    }

    public Selection raw(String statement, Object... args) {
        if (mBuilder.length() != 0) {
            mBuilder.append(' ');
        }
        mBuilder.append(statement);
        if (args != null
                && args.length > 0) {
            for (Object arg: args) {
                mArgs.add(str(arg));
            }
        }
        return this;
    }

    public Selection and() {
        mBuilder.append(" AND ");
        return this;
    }

    public Selection or() {
        mBuilder.append(" OR ");
        return this;
    }

    public Selection groupStart() {
        mBuilder.append("(");
        return this;
    }

    public Selection groupEnd() {
        mBuilder.append(')');
        return this;
    }

    @Override
    public String getStatement() {
        if (mBuilder.length() == 0) {
            return null;
        }
        return mBuilder.toString();
    }

    @Override
    public String[] getArguments() {
        final int size = mArgs.size();
        if (size == 0) {
            return null;
        }
        final String[] args = new String[size];
        mArgs.toArray(args);
        return args;
    }

    @Override
    public boolean isEmpty() {
        return mBuilder.length() == 0;
    }

    private static String str(Object what) {
        return String.valueOf(what);
    }
}
