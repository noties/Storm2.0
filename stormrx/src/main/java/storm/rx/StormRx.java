package storm.rx;

import storm.core.Storm;
import storm.core.StormObject;
import storm.core.StormQuery;
import storm.db.Database;
import storm.query.Query;
import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormRx extends Storm {

    public static StormRx newInstance(Database.Configuration configuration) {
        return new StormRx(configuration);
    }

    private StormRx(Database.Configuration configuration) {
        super(configuration);
    }

    @Override
    public <T extends StormObject> StormQueryRx<T> query(Class<T> table) {
        return new StormQueryRx<>(super.query(table));
    }

    @Override
    public <T extends StormObject> StormQueryRx<T> query(Class<T> table, String selection, Object... args) {
        return new StormQueryRx<>(super.query(table, selection, args));
    }

    @Override
    public <T extends StormObject> StormQueryRx<T> query(Class<T> table, Selection selection) {
        return new StormQueryRx<>(super.query(table, selection));
    }

    @Override
    public <T extends StormObject> StormQueryRx<T> query(Class<T> table, Query query) {
        return new StormQueryRx<>(super.query(table, query));
    }
}
