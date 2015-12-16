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
    }

    static final StormQueryDispatcher QUERY_DISPATCHER = new StormQueryDispatcherImpl();

    public static Storm newInstance(Database.Configuration configuration) {
        return new Storm(configuration);
    }

    private final Database mDatabase;
    private final StormSchemeFactory mSchemeFactory;
    private final StormParserFactory mParserFactory;
    private final StormInstanceCreators mInstanceCreators;

    private Storm(Database.Configuration configuration) {
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

    // todo table: name, notificationUri

    public <T extends StormObject> StormParser<T> parser(Class<T> table) {
        try {
            return mParserFactory.provide(table);
        } catch (StormParserException e) {
            throw new RuntimeException(e);
        }
    }


    public <T extends StormObject> StormQuery<T> query(Class<T> table) {
        return new StormQuery<T>(this, table, new Query().from(null), QUERY_DISPATCHER);
    }

    public <T extends StormObject> StormQuery<T> query(Class<T> table, String selection, Object... args) {
        return new StormQuery<T>(this, table, new Query().from(null).where(new Selection().raw(selection, args)), QUERY_DISPATCHER);
    }

    public <T extends StormObject> StormQuery<T> query(Class<T> table, Selection selection) {
        return new StormQuery<T>(this, table, new Query().from(null).where(selection), QUERY_DISPATCHER);
    }

    public <T extends StormObject> StormQuery<T> query(Class<T> table, Query query) {
        return new StormQuery<T>(this, table, query, QUERY_DISPATCHER);
    }


    public <T extends StormObject> void save(T object) {

    }

    public <T extends StormObject> void save(Collection<T> objects) {

    }
}
