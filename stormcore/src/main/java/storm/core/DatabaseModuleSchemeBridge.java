package storm.core;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import storm.db.DatabaseModuleScheme;
import storm.parser.scheme.StormScheme;

/**
 * Created by Dimitry Ivanov on 14.12.2015.
 */
class DatabaseModuleSchemeBridge extends DatabaseModuleScheme {

    private final StormScheme mScheme;

    DatabaseModuleSchemeBridge(StormScheme scheme) {
        this.mScheme = scheme;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final List<String> statements = mScheme.onCreate();
        if (statements != null
                && statements.size() > 0) {
            for (String statement: statements) {
                db.execSQL(statement);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // sometimes `onUpgrade` is called, but for some reason there is not such table
        // it's primary a human error (wrong version to db, etc)
        // but there is a simple way to check if a table exists:
        // `select * from sqlite_master where type = 'table' AND name = '%s'`, where %s is your table name
        // if there is no such table it's wise to call `onCreate` for this table
        // This way is better that adding `IF EXISTS`, because it will leave the db in wrong state
        // which eventually will crash the app

        final List<String> statements = mScheme.onUpgrade(oldVersion, newVersion);
        if (statements != null
                && statements.size() > 0) {
            for (String statement: statements) {
                db.execSQL(statement);
            }
        }
    }
}
