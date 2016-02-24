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
 *
 * Does not introduces background processing (all operations are executed synchronously)
 *
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

    /**
     * Registers a {@link DatabaseModule} for this instance. It will be passed to {@link Database}.
     * Could me useful to receive callbacks from {@link android.database.sqlite.SQLiteOpenHelper}.
     * Also could be useful to execute pragma statements or prefill database on creation
     *
     * @see Database
     * @see DatabaseModule
     * @see StormPrefillDatabaseModule
     * @see storm.db.DatabaseModuleConfiguration
     * @see storm.db.DatabaseModuleScheme
     * @see storm.db.pragma.PragmasModule
     * @see storm.db.pragma.Pragma
     * @see #registerDatabaseModules(Collection)
     *
     * @param module {@link DatabaseModule}
     * @return current {@link Storm} instance to which module is registered
     */
    public Storm registerDatabaseModule(DatabaseModule module) {
        mDatabase.registerModule(module);
        return this;
    }

    /**
     * @see #registerDatabaseModule(DatabaseModule)
     *
     * @param modules to be registered
     * @return current instance
     */
    public Storm registerDatabaseModules(Collection<? extends DatabaseModule> modules) {
        mDatabase.registerModules(modules);
        return this;
    }

    /**
     * The entry point to attach tables for this Storm instance & underlined {@link Database}.
     * Will call {@link #scheme(Class)} to obtain generated class and throw {@link StormException}
     * if was unsuccessful.
     *
     * Every table that should be created with this instance and associated {@link Database}
     * and/or updated/queried must be registered via this method.
     *
     * @see StormObject
     * @see StormScheme
     * @see DatabaseModuleSchemeBridge
     *
     * @param tableClass table for database
     * @param <T> object that implements {@link StormObject}
     * @return current instance
     * @throws StormException if {@link #scheme(Class)} could not obtain generated StormScheme class
     */
    public <T extends StormObject> Storm registerTable(Class<T> tableClass) throws StormException {
        mDatabase.registerModule(new DatabaseModuleSchemeBridge(scheme(tableClass)));
        return this;
    }

    /**
     * @see Database
     *
     * @return {@link Database} with which current instance is associated
     */
    public Database database() {
        return mDatabase;
    }


    /**
     * @see StormParser
     * @see StormParserFactory
     * @see StormObject
     *
     * @param table for which to obtain {@link StormParser}
     * @param <T> object that implements {@link StormObject}
     * @return {@link StormParser} for specified table
     */
    public <T extends StormObject> StormParser<T> parser(Class<T> table) {
        return mParserFactory.provide(table);
    }

    /**
     * Method to obtain {@link StormConverter} for specified table
     *
     * @see #converter(Class, StormParser)
     */
    public <T extends StormObject> StormConverter<T> converter(Class<T> table) throws StormException {
        return converter(table, parser(table));
    }

    /**
     * Helper method to obtain {@link StormConverter} from already obtained {@link StormParser} instance
     *
     * @see StormConverter
     * @see #parser(Class)
     * @see #converter(Class)
     *
     * @param table for which to obtain {@link StormConverter}
     * @param parser pre-obtained {@link StormParser} for table
     * @param <T> object that implements {@link StormObject}
     * @return {@link StormConverter} for specified table
     * @throws StormException if {@link StormConverter} could not be obtained
     */
    public <T extends StormObject> StormConverter<T> converter(Class<T> table, StormParser<T> parser) throws StormException {
        try {
            return parser.converter();
        } catch (StormParserException e) {
            throw StormException.newInstance(e, "Exception obtaining converter for a class: `%s`", table.getName());
        }
    }

    /**
     * Method to obtain {@link StormMetadata} for specified table
     *
     * @see #metadata(Class, StormParser)
     */
    public <T extends StormObject> StormMetadata<T> metadata(Class<T> table) throws StormException {
        try {
            return mParserFactory.provide(table).metadata();
        } catch (StormParserException e) {
            throw StormException.newInstance(e, "Exception obtaining metadata for a class: `%s`", table.getName());
        }
    }

    /**
     * Helper method to obtain {@link StormMetadata} from pre-obtained {@link StormParser}
     *
     * @see StormMetadata
     * @see StormParser
     * @see #metadata(Class)
     *
     * @param table for whicj to obtain {@link StormMetadata}
     * @param parser pre-obtained {@link StormParser} for this table
     * @param <T> object of type {@link StormObject}
     * @return {@link StormMetadata} for specified table
     * @throws StormException
     */
    public <T extends StormObject> StormMetadata<T> metadata(Class<T> table, StormParser<T> parser) throws StormException {
        try {
            return parser.metadata();
        } catch (StormParserException e) {
            throw StormException .newInstance(e, "Exception obtaining metadata for a class: %s", table.getName());
        }
    }

    /**
     * Method to obtain {@link StormScheme} for specified table
     *
     * @see StormScheme
     * @see StormParser
     *
     * @param table for which to obtain {@link StormScheme}
     * @param <T> object of type {@link StormObject}
     * @return {@link StormScheme} for specified table
     * @throws StormException
     */
    public <T extends StormObject> StormScheme scheme(Class<T> table) throws StormException {
        try {
            return mParserFactory.provide(table).scheme();
        } catch (StormParserException e) {
            throw StormException.newInstance(e, "Exception obtaining scheme for a class: `%s`", table.getName());
        }
    }

    /**
     * Helper method to obtain table name information for specified table
     *
     * @see #metadata(Class)
     *
     * @param table to obtain name for
     * @param <T> object of type {@link StormObject}
     * @return name of the table
     */
    public <T extends StormObject> String tableName(Class<T> table) {
        return metadata(table).tableName();
    }

    /**
     * Helper method to obtain notification {@link Uri} for this table
     *
     * @see #metadata(Class)
     * @see #notifyChange(Class)
     *
     * @param table for which to obtain notification uri
     * @param <T> object of type {@link StormObject}
     * @return update {@link Uri} for specified table
     */
    public <T extends StormObject> Uri notificationUri(Class<T> table) {
        return metadata(table).notificationUri();
    }

    /**
     * Helper method to notify database about change
     *
     * @see #notificationUri(Class)
     * @see Database#notify(Uri)
     *
     * @param table for which notification should be triggered
     * @param <T> object of type {@link StormObject}
     */
    public <T extends StormObject> void notifyChange(Class<T> table) {
        mDatabase.notify(notificationUri(table));
    }


    /**
     * QUERY. Generates query starting with `SELECT * FROM %tableName%` (where %table_name% is the name of the querying table)
     * The query can be specified with fluent interface, for example
     * {@code Storm.query(StormObject.class).limit(10).offset(50).asCachedIterator(5) }
     * If you wish to `offset` without `limit` pass `-1` as limit parameter
     *
     * @see StormQuery
     * @see Query
     * @see Query#allFrom(String)
     *
     * @see #query(Class, Selection)
     * @see #query(Class, Query)
     * @see #query(Class, String, Object...)
     *
     * @see #simpleQuery(Class, Query)
     * @see #simpleQuery(Class, String)
     *
     * @param table for which to execute the query
     * @param <T> object of type {@link StormObject}
     * @return created {@link StormQuery} object to proceed with operation
     */
    public <T extends StormObject> StormQuery<T> query(Class<T> table) {
        return new StormQuery<>(
                this,
                table,
                Query.allFrom(tableName(table)),
                mDispatchers.queryDispatcher()
        );
    }

    /**
     * QUERY. Generates query statement starting with `SELECT * FROM %table_name% WHERE %selection%`
     * (where %table_name% is name of the table & %selection% is the passed selection)
     *
     * {@code Storm.query(StormObject.class, "some_column > ?", 54L).asOne(); }
     * {@code Storm.query(StormObject.class, "some_column < ? OR other_column > ?", 67L, 43).asOne(); }
     *
     * @see #query(Class)
     * @see #query(Class, Selection)
     * @see #query(Class, Query)
     *
     * @see #simpleQuery(Class, Query)
     * @see #simpleQuery(Class, String)
     *
     * @see StormQuery
     * @see Query
     * @see Selection
     *
     * @param table for which to execute the query
     * @param selection raw SQL selection
     * @param args arguments for SQL selection (might be empty)
     * @param <T> object of type {@link StormObject}
     * @return {@link StormQuery} object to proceed with operation
     */
    public <T extends StormObject> StormQuery<T> query(Class<T> table, String selection, Object... args) {
        return new StormQuery<>(
                this,
                table,
                Query.allFrom(tableName(table)).where(new Selection().raw(selection, args)),
                mDispatchers.queryDispatcher()
        );
    }

    /**
     * QUERY. Generates query statement starting with `SELECT * FROM %table_name% %selection%`
     * (where %table_name% is the table of the table & %selection% is passed {@link Selection})
     *
     * {@code Storm.query(StormObject.class, new Selection().greater("column", 66)).asOne(); }
     *
     * @see #query(Class)
     * @see #query(Class, String, Object...)
     * @see #query(Class, Query)
     *
     * @see #simpleQuery(Class, Query)
     * @see #simpleQuery(Class, String)
     *
     * @see StormQuery
     * @see Query
     * @see Selection
     *
     * @param table for which to execute the query
     * @param selection {@link Selection} for the query
     * @param <T> object of type {@link StormObject}
     * @return {@link StormQuery} to proceed with operation
     */
    public <T extends StormObject> StormQuery<T> query(Class<T> table, Selection selection) {
        return new StormQuery<>(
                this,
                table,
                Query.allFrom(tableName(table)).where(selection),
                mDispatchers.queryDispatcher()
        );
    }

    /**
     * QUERY. Creates a {@link StormQuery} object from passed {@link Query} object.
     * Please note, that this method would not create any starting message for SQL.
     * The passed {@link Query} must be a valid one. It should have table name & columns specified.
     *
     * @see #query(Class)
     * @see #query(Class, String, Object...)
     * @see #query(Class, Selection)
     *
     * @see #simpleQuery(Class, Query)
     * @see #simpleQuery(Class, String)
     *
     * @see Query
     * @see Query#allFrom(String)
     * @see StormQuery
     *
     * @param table for which to execute query
     * @param query {@link Query} valid query object
     * @param <T> object of type {@link StormObject}
     * @return {@link StormQuery} to proceed with operation
     */
    // Full filled Query object (with tableName)
    public <T extends StormObject> StormQuery<T> query(Class<T> table, Query query) {
        return new StormQuery<>(
                this,
                table,
                query,
                mDispatchers.queryDispatcher()
        );
    }


    /**
     *
     * @param table
     * @param column
     * @param <T>
     * @return
     */
    public <T extends StormObject> StormSimpleQuery<T> simpleQuery(Class<T> table, String column) {
        return new StormSimpleQuery<>(
                this,
                table,
                new Query().select(column).from(tableName(table)),
                mDispatchers.simpleQueryDispatcher()
        );
    }

    // Full filled Query object (with tableName)
    public <T extends StormObject> StormSimpleQuery<T> simpleQuery(Class<T> table, Query query) {
        return new StormSimpleQuery<>(
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
