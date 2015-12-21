package storm.core;

import android.net.Uri;

import java.util.Collection;

import storm.db.Database;
import storm.db.DatabaseModule;
import storm.parser.StormInstanceCreator;
import storm.parser.StormParser;
import storm.parser.StormParserException;
import storm.parser.StormParserFactory;
import storm.query.Query;
import storm.query.Selection;
import storm.scheme.StormSchemeException;
import storm.scheme.StormSchemeFactory;

/**
 * Created by Dimitry Ivanov on 30.11.2015.
 */
public class Storm {

    public static Storm newInstance(Database.Configuration configuration) {
        return new Storm(new StormDispatchersImpl(), configuration);
    }

    private final StormDispatchers mDispatchers;
    private final Database mDatabase;
    private final StormSchemeFactory mSchemeFactory;
    private final StormParserFactory mParserFactory;
    private final StormInstanceCreators mInstanceCreators;

    protected Storm (Database.Configuration configuration) {
        this(new StormDispatchersImpl(), configuration);
    }

    Storm(StormDispatchers dispatchers, Database.Configuration configuration) {
        this.mDispatchers = dispatchers;
        this.mDatabase = new Database(configuration);
        this.mInstanceCreators = new StormInstanceCreators();
        this.mSchemeFactory = new StormSchemeFactory();
        this.mParserFactory = new StormParserFactory(new StormParserFactory.InstanceCreatorProvider() {
            @Override
            public <T> StormInstanceCreator<T> provide(Class<T> cl) {
                //noinspection unchecked
                return (StormInstanceCreator<T>) mInstanceCreators.get((Class<StormObject>) cl);
            }
        });
    }

    public Storm registerDatabaseModule(DatabaseModule module) {
        mDatabase.registerModule(module);
        return this;
    }

    public Storm registerDatabaseModules(Collection<? extends DatabaseModule> modules) {
        mDatabase.registerModules(modules);
        return this;
    }

    public <T extends StormObject> Storm registerTable(Class<T> tableClass) throws StormException {

        try {
            mDatabase.registerModule(new DatabaseModuleSchemeBridge(mSchemeFactory.provide(tableClass)));
        } catch (StormSchemeException e) {
            throw StormException.newInstance(e, "Exception obtaining Scheme for a class: `%s`", tableClass.getName());
        }

        return this;
    }

    public <T extends StormObject> Storm registerInstanceCreator(Class<T> table, StormInstanceCreator<T> instanceCreator) {
        mInstanceCreators.put(table, instanceCreator);
        return this;
    }

    public Database database() {
        return mDatabase;
    }


    public <T extends StormObject> StormParser<T> parser(Class<T> table) throws StormException {
        try {
            return mParserFactory.provide(table);
        } catch (StormParserException e) {
            throw StormException.newInstance(e, "Exception obtaining Parser for a class: `%s`", table.getName());
        }
    }


    public <T extends StormObject> String tableName(Class<T> table) {
        return parser(table).getMetadata().getTableName();
    }

    public <T extends StormObject> Uri notificationUri(Class<T> table) {
        return parser(table).getMetadata().getNotificationUri();
    }

    public <T extends StormObject> void notifyChange(Class<T> table) {
        mDatabase.notify(notificationUri(table));
    }

    public <T extends StormObject> StormQuery<T> query(Class<T> table) {
        return new StormQuery<T>(
                this,
                table,
                Query.allFrom(tableName(table)),
                mDispatchers.queryDispatcher()
        );
    }

    public <T extends StormObject> StormQuery<T> query(Class<T> table, String selection, Object... args) {
        return new StormQuery<T>(
                this,
                table,
                Query.allFrom(tableName(table)).where(new Selection().raw(selection, args)),
                mDispatchers.queryDispatcher()
        );
    }

    public <T extends StormObject> StormQuery<T> query(Class<T> table, Selection selection) {
        return new StormQuery<T>(
                this,
                table,
                Query.allFrom(tableName(table)).where(selection),
                mDispatchers.queryDispatcher()
        );
    }

    // Full filled Query object (with tableName)
    public <T extends StormObject> StormQuery<T> query(Class<T> table, Query query) {
        return new StormQuery<T>(
                this,
                table,
                query,
                mDispatchers.queryDispatcher()
        );
    }


    public <T extends StormObject> StormSimpleQuery<T> simpleQuery(Class<T> table, String column) {
        return new StormSimpleQuery<T>(
                this,
                table,
                new Query().select(column).from(tableName(table)),
                mDispatchers.simpleQueryDispatcher()
        );
    }

    // Full filled Query object (with tableName)
    public <T extends StormObject> StormSimpleQuery<T> simpleQuery(Class<T> table, Query query) {
        return new StormSimpleQuery<T>(
                this,
                table,
                query,
                mDispatchers.simpleQueryDispatcher()
        );
    }

    public <T extends StormObject> StormCount<T> count(Class<T> table) {
        return new StormCount<>(
                this,
                table,
                new Selection(),
                mDispatchers.countDispatcher()
        );
    }

    public <T extends StormObject> StormCount<T> count(Class<T> table, Selection selection) {
        return new StormCount<>(
                this,
                table,
                selection,
                mDispatchers.countDispatcher()
        );
    }

    public <T extends StormObject> StormCount<T> count(Class<T> table, String where, Object... args) {
        return new StormCount<>(
                this,
                table,
                new Selection().raw(where, args),
                mDispatchers.countDispatcher()
        );
    }


    public <T extends StormObject> StormSaveOne<T> save(T value) {
        return new StormSaveOne<>(
                this,
                value,
                mDispatchers.saveOneDispatcher()
        );
    }

    public <T extends StormObject> StormSaveMany<T> save(Collection<T> values) throws StormException {
        return new StormSaveMany<>(
                this,
                values,
                mDispatchers.saveManyDispatcher()
        );
    }


    public <T extends StormObject> StormUpdateOne<T> update(T value) {
        return new StormUpdateOne<>(
                this,
                value,
                mDispatchers.updateOneDispatcher()
        );
    }

    public <T extends StormObject> StormUpdateMany<T> update(Collection<T> values) throws StormException {
        return new StormUpdateMany<>(
                this,
                values,
                mDispatchers.updateManyDispatcher()
        );
    }


    public <T extends StormObject> StormFill<T> fill(T value) throws StormException {
        return new StormFill<>(
                this,
                new Selection(),
                value,
                mDispatchers.fillDispatcher()
        );
    }

    public <T extends StormObject> StormFill<T> fill(T value, Selection selection) throws StormException {
        return new StormFill<>(
                this,
                selection,
                value,
                mDispatchers.fillDispatcher()
        );
    }


    public <T extends StormObject> StormDeleteAll<T> deleteAll(Class<T> table) {
        return new StormDeleteAll<>(
                this,
                table,
                null,
                mDispatchers.deleteDispatcher()
        );
    }

    public <T extends StormObject> StormDelete<T> delete(Class<T> table) {
        return new StormDelete<>(
                this,
                table,
                new Selection(),
                mDispatchers.deleteDispatcher()
        );
    }

    public <T extends StormObject> StormDelete<T> delete(Class<T> table, Selection selection) {
        return new StormDelete<>(
                this,
                table,
                selection,
                mDispatchers.deleteDispatcher()
        );
    }

    public <T extends StormObject> StormDelete<T> delete(Class<T> table, String where, Object... args) {
        return new StormDelete<>(
                this,
                table,
                new Selection().raw(where, args),
                mDispatchers.deleteDispatcher()
        );
    }
}
