package storm.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dimitry Ivanov on 29.09.2015.
 */
public class Query implements IStatementBuilder {

    // all columns from the table
    public static Query allFrom(String table) {
        return new Query().select().from(table);
    }

    private final StringBuilder mBuilder;
    private final List<String> mArgs;

    public Query() {
        this.mBuilder   = new StringBuilder();
        this.mArgs      = new ArrayList<>();
    }

    public Query select(String... columns) {
        mBuilder.append("SELECT ")
                .append(createColumns(columns));
        return this;
    }

    public Query from(String table) {
        mBuilder.append(" FROM ")
                .append(table);
        return this;
    }

    // empty to be used in subqueries
    public Query from() {
        mBuilder.append(" FROM");
        return this;
    }

    public Query where(String condition, Object... args) {
        mBuilder.append(" WHERE ")
                .append(condition);

        if (args != null
                && args.length > 0) {
            for (Object arg: args) {
                mArgs.add(String.valueOf(arg));
            }
        }

        return this;
    }

    public Query where(Selection selection) {
        mBuilder.append(" WHERE ")
                .append(selection.getStatement());
        if (selection.getArguments() != null) {
            Collections.addAll(mArgs, selection.getArguments());
        }
        return this;
    }

    public Query groupBy(String groupBy, String having, Object... havingArgs) {
        mBuilder.append(" GROUP BY ")
                .append(groupBy)
                .append(" HAVING ")
                .append(having);

        if (havingArgs != null
                && havingArgs.length > 0) {
            for (Object arg: havingArgs) {
                mArgs.add(String.valueOf(arg));
            }
        }

        return this;
    }

    public Query orderBy(String column, Sorting sorting) {
        mBuilder.append(" ORDER BY ")
                .append(column)
                .append(' ')
                .append(sorting.name());
        return this;
    }

    public Query orderBy(OrderBy first, OrderBy... others) {
        mBuilder.append(" ORDER BY ")
                .append(first.column)
                .append(' ')
                .append(first.sorting.name());

        if (others != null
                && others.length > 0) {
            for (OrderBy orderBy: others) {
                mBuilder.append(", ")
                        .append(orderBy.column)
                        .append(' ')
                        .append(orderBy.sorting.name());
            }
        }
        return this;
    }

    public Query limit(long limit) {
        mBuilder.append(" LIMIT ")
                .append('?');
        mArgs.add(String.valueOf(limit));
        return this;
    }

    // inner join with USING
    public Query join(String table, String column, String... additionalColumns) {
        mBuilder.append(" JOIN ")
                .append(table)
                .append(" USING (")
                .append(column);

        if (additionalColumns != null
                && additionalColumns.length > 0) {
            mBuilder.append(", ")
                    .append(createColumns(additionalColumns));
        }
        mBuilder.append(")");
        return this;
    }

    // inner join with condition
    public Query join(String table, Selection condition) {
        mBuilder.append(" JOIN ")
                .append(table)
                .append(" ON ")
                .append(condition.getStatement());
        if (condition.getArguments() != null) {
            Collections.addAll(mArgs, condition.getArguments());
        }
        return this;
    }

    // empty to join from a subquery
    public Query join() {
        mBuilder.append(" JOIN");
        return this;
    }

    public Query groupStart() {
        mBuilder.append(" (");
        return this;
    }

    public Query groupEnd() {
        mBuilder.append(")");
        return this;
    }

    public Query raw(String statement, Object... args) {
        mBuilder.append(statement);
        if (args != null
                && args.length > 0) {
            for (Object arg: args) {
                mArgs.add(String.valueOf(arg));
            }
        }
        return this;
    }

    private static String createColumns(String... columns) {
        if (columns == null
                || columns.length == 0) {
            return "*";
        }
        final StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (String column: columns) {
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append(", ");
            }
            builder.append(column);
        }
        return builder.toString();
    }

    @Override
    public String getStatement() {
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
}
