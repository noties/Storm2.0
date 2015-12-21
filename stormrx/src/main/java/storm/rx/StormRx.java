package storm.rx;

import java.util.Collection;

import storm.core.Storm;
import storm.core.StormObject;
import storm.db.Database;
import storm.db.DatabaseModule;
import storm.parser.StormInstanceCreator;
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
    public StormRx registerDatabaseModule(DatabaseModule module) {
        return (StormRx) super.registerDatabaseModule(module);
    }

    @Override
    public StormRx registerDatabaseModules(Collection<? extends DatabaseModule> modules) {
        return (StormRx) super.registerDatabaseModules(modules);
    }

    @Override
    public <T extends StormObject> StormRx registerTable(Class<T> tableClass) {
        return (StormRx) super.registerTable(tableClass);
    }

    @Override
    public <T extends StormObject> StormRx registerInstanceCreator(Class<T> table, StormInstanceCreator<T> instanceCreator) {
        return (StormRx) super.registerInstanceCreator(table, instanceCreator);
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
