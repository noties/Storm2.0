package storm.core;

import android.net.Uri;

import java.util.Collection;

import storm.db.Database;
import storm.db.DatabaseModule;
import storm.parser.StormParser;
import storm.parser.StormParserException;
import storm.parser.StormParserFactory;
import storm.parser.converter.StormConverter;
import storm.parser.metadata.StormMetadata;
import storm.parser.scheme.StormScheme;
import storm.query.Query;
import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 30.11.2015.
 */
public class Storm {

    public static Storm newInstance(Database.Configuration configuration) {
        return new Storm(new StormDispatchersImpl(), configuration);
    }

    private final StormDispatchers mDispatchers;
    private final Database mDatabase;
    private final StormParserFactory mParserFactory;

    protected Storm (Database.Configuration configuration) {
        this(new StormDispatchersImpl(), configuration);
    }

    Storm(StormDispatchers dispatchers, Database.Configuration configuration) {
        this.mDispatchers = dispatchers;
        this.mDatabase = new Database(configuration);
        this.mParserFactory = new StormParserFactory();
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
        mDatabase.registerModule(new DatabaseModuleSchemeBridge(scheme(tableClass)));
        return this;
    }

    public Database database() {
        return mDatabase;
    }



    public <T extends StormObject> StormParser<T> parser(Class<T> table) {
        return mParserFactory.provide(table);
    }

    public <T extends StormObject> StormConverter<T> converter(Class<T> table) throws StormException {
        return converter(table, parser(table));
    }

    public <T extends StormObject> StormConverter<T> converter(Class<T> table, StormParser<T> parser) throws StormException {
        try {
            return parser.converter();
        } catch (StormParserException e) {
            throw StormException.newInstance(e, "Exception obtaining converter for a class: `%s`", table.getName());
        }
    }

    public <T extends StormObject> StormMetadata<T> metadata(Class<T> table) throws StormException {
        try {
            return mParserFactory.provide(table).metadata();
        } catch (StormParserException e) {
            throw StormException.newInstance(e, "Exception obtaining metadata for a class: `%s`", table.getName());
        }
    }

    public <T extends StormObject> StormMetadata<T> metadata(Class<T> table, StormParser<T> parser) throws StormException {
        try {
            return parser.metadata();
        } catch (StormParserException e) {
            throw StormException .newInstance(e, "Exception obtaining metadata for a class: %s", table.getName());
        }
    }

    public <T extends StormObject> StormScheme scheme(Class<T> table) throws StormException {
        try {
            return mParserFactory.provide(table).scheme();
        } catch (StormParserException e) {
            throw StormException.newInstance(e, "Exception obtaining scheme for a class: `%s`", table.getName());
        }
    }


    public <T extends StormObject> String tableName(Class<T> table) {
        return metadata(table).tableName();
    }

    public <T extends StormObject> Uri notificationUri(Class<T> table) {
        return metadata(table).notificationUri();
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

    public <T extends StormObject> StormFill<T> fill(T value, String selection, Object... args) throws StormException {
        return new StormFill<>(
                this,
                new Selection().raw(selection, args),
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
