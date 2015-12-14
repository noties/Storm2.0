package storm.core;

import java.util.Collection;

import storm.db.Database;
import storm.db.DatabaseModule;
import storm.db.pragma.ForeignKeysPragma;
import storm.db.pragma.PragmasModule;
import storm.parser.StormParserFactory;
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
                .registerTableClass((Class<StormObject>) null);
    }

    public static Storm newInstance(Database.Configuration configuration) {
        return new Storm(configuration);
    }

    private final Database mDatabase;
    private final StormSchemeFactory mSchemeFactory;
    private final StormParserFactory mParserFactory;

    private Storm(Database.Configuration configuration) {
        this.mDatabase = new Database(configuration);
        this.mSchemeFactory = new StormSchemeFactory();
        this.mParserFactory = new StormParserFactory();
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

    public Database database() {
        return mDatabase;
    }

    public <T extends StormObject> void query(Class<T> table) {

    }
}
