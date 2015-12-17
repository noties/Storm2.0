package storm.core;

import java.util.Collection;

import storm.db.Database;
import storm.db.DatabaseModule;
import storm.db.pragma.ForeignKeysPragma;
import storm.db.pragma.PragmasModule;
import storm.parser.StormInstanceCreator;
import storm.parser.StormParser;
import storm.parser.StormParserException;
import storm.parser.StormParserFactory;
import storm.query.Query;
import storm.query.Selection;
import storm.query.Sorting;
import storm.scheme.StormSchemeException;
import storm.scheme.StormSchemeFactory;

/**
 * Created by Dimitry Ivanov on 30.11.2015.
 */
public class Storm {

    static {
        final Storm storm = Storm.newInstance(null)
                .registerDatabaseModule(PragmasModule.of(ForeignKeysPragma.of(true)))
                .registerDatabaseModule(null)
                .registerTableClass((Class<StormObject>) null)
                .registerInstanceCreator((Class<StormObject>) null, new ReflectionInstanceCreator<StormObject>(null));

        newInstance(null)
                .query(null, new Selection().equals("col", 23).and().notIn("col", 12, 66));

        newInstance(null)
                .query(null, "some_col = ?", 77)
                .orderBy("col", Sorting.ASC)
                .limit(3)
                .asList();

        newInstance(null)
                .delete(null/*SomeTabel.class*/, "somw_col < ?", 9)
                .execute();

        newInstance(null)
                .count(null)
                .equals("col", null)
                .glob(null, null)
                .execute();

        newInstance(null)
                .count(null, "someCol = ?", "someValue")
                .execute();

        newInstance(null)
                .count(null, new Selection().glob("glob", "pattern"))
                .execute();
    }

    public static Storm newInstance(Database.Configuration configuration) {
        return new Storm(configuration);
    }

    static final StormQueryDispatcher QUERY_DISPATCHER = new StormQueryDispatcherImpl();
    static final StormDeleteDispatcher DELETE_DISPATCHER = new StormDeleteDispatcherImpl();
    static final StormCountDispatcher COUNT_DISPATCHER = new StormCountDispatcherImpl();
    static final StormSaveOneDispatcher SAVE_ONE_DISPATCHER = new StormSaveOneDispatcherImpl();
    static final StormSaveManyDispatcher SAVE_MANY_DISPATCHER = new StormSaveManyDispatcherImpl();
    static final StormFillDispatcher FILL_DISPATCHER = new StormFillDispatcherImpl();

    private final Database mDatabase;
    private final StormSchemeFactory mSchemeFactory;
    private final StormParserFactory mParserFactory;
    private final StormInstanceCreators mInstanceCreators;

    Storm(Database.Configuration configuration) {
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

    public Storm registerDatabaseModules(DatabaseModule... modules) {
        mDatabase.registerModules(modules);
        return this;
    }

    public Storm registerDatabaseModules(Collection<DatabaseModule> modules) {
        mDatabase.registerModules(modules);
        return this;
    }

    public <T extends StormObject> Storm registerTableClass(Class<T> tableClass) {

        try {
            mDatabase.registerModule(new DatabaseModuleSchemeBridge(mSchemeFactory.provide(tableClass)));
        } catch (StormSchemeException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public <T extends StormObject> Storm registerTableClasses(Class<T>... tableClasses) {

        for (Class<T> cl: tableClasses) {
            try {
                mDatabase.registerModule(new DatabaseModuleSchemeBridge(mSchemeFactory.provide(cl)));
            } catch (StormSchemeException e) {
                throw new RuntimeException(e);
            }
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

    // todo table: name, notificationUri, isPrimaryKeyAutoincrement, primaryKeySelection

    public <T extends StormObject> StormParser<T> parser(Class<T> table) {
        try {
            return mParserFactory.provide(table);
        } catch (StormParserException e) {
            throw new RuntimeException(e);
        }
    }


    <T extends StormObject> String tableName(Class<T> cl) {
        return parser(cl).getMetadata().getTableName();
    }


    public <T extends StormObject> StormQuery<T> query(Class<T> table) {
        return new StormQuery<T>(
                this,
                table,
                new Query().select().from(tableName(table)),
                QUERY_DISPATCHER
        );
    }

    public <T extends StormObject> StormQuery<T> query(Class<T> table, String selection, Object... args) {
        return new StormQuery<T>(
                this,
                table,
                new Query().select().from(tableName(table)).where(new Selection().raw(selection, args)),
                QUERY_DISPATCHER
        );
    }

    public <T extends StormObject> StormQuery<T> query(Class<T> table, Selection selection) {
        return new StormQuery<T>(
                this,
                table,
                new Query().select().from(tableName(table)).where(selection),
                QUERY_DISPATCHER
        );
    }

    public <T extends StormObject> StormQuery<T> query(Class<T> table, Query query) {
        return new StormQuery<T>(this, table, query, QUERY_DISPATCHER);
    }


    public <T extends StormObject> StormCount<T> count(Class<T> table) {
        return new StormCount<>(
                this,
                table,
                new Selection(),
                COUNT_DISPATCHER
        );
    }

    public <T extends StormObject> StormCount<T> count(Class<T> table, Selection selection) {
        return new StormCount<>(
                this,
                table,
                selection,
                COUNT_DISPATCHER
        );
    }

    public <T extends StormObject> StormCount<T> count(Class<T> table, String where, Object... args) {
        return new StormCount<>(
                this,
                table,
                new Selection().raw(where, args),
                COUNT_DISPATCHER
        );
    }


    public <T extends StormObject> StormSaveOne<T> save(T value) {
        return new StormSaveOne<>(
                this,
                value,
                SAVE_ONE_DISPATCHER
        );
    }

    public <T extends StormObject> StormSaveMany<T> save(Collection<T> values) {
        return new StormSaveMany<>(
                this,
                values,
                SAVE_MANY_DISPATCHER
        );
    }


    public <T extends StormObject> StormUpdateOne<T> update(T value) {
        return new StormUpdateOne<>(
                this,
                value,
                null
        );
    }

    public <T extends StormObject> StormUpdateMany<T> update(Collection<T> values) {
        return new StormUpdateMany<>(
                this,
                values,
                null
        );
    }


    public <T extends StormObject> StormFill<T> fill(T value) {
        return new StormFill<>(
                this,
                new Selection(),
                value,
                FILL_DISPATCHER
        );
    }

    public <T extends StormObject> StormFill<T> fill(T value, Selection selection) {
        return new StormFill<>(
                this,
                selection,
                value,
                FILL_DISPATCHER
        );
    }


    public <T extends StormObject> StormDeleteAll<T> deleteAll(Class<T> table) {
        return new StormDeleteAll<>(
                this,
                table,
                null,
                DELETE_DISPATCHER
        );
    }

    public <T extends StormObject> StormDelete<T> delete(Class<T> table) {
        return new StormDelete<>(
                this,
                table,
                new Selection(),
                DELETE_DISPATCHER
        );
    }

    public <T extends StormObject> StormDelete<T> delete(Class<T> table, Selection selection) {
        return new StormDelete<>(
                this,
                table,
                selection,
                DELETE_DISPATCHER
        );
    }

    public <T extends StormObject> StormDelete<T> delete(Class<T> table, String where, Object... args) {
        return new StormDelete<>(
                this,
                table,
                new Selection().raw(where, args),
                DELETE_DISPATCHER
        );
    }
}
