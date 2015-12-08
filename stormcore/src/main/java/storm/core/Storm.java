package storm.core;

import storm.db.Database;
import storm.lazy.Lazy;

/**
 * Created by Dimitry Ivanov on 30.11.2015.
 */
public class Storm {

    public static Storm newInstance() {
        return new Storm();
    }

    private final Lazy<Database> mDatabase;

    private Storm() {
        this.mDatabase = createDatabaseLazy(null);
    }

    public Database getDatabase() {
        return mDatabase.get();
    }

    private static Lazy<Database> createDatabaseLazy(final Database.Configuration configuration) {
        return new Lazy<>(new Lazy.LazyProvider<Database>() {
            @Override
            public Database provide() {
                return new Database(configuration)
                        .registerModules();
            }
        });
    }

}
