package storm.core;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import storm.db.DatabaseModuleScheme;
import storm.scheme.StormScheme;
import storm.scheme.StormSchemeException;

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
        try {
            final List<String> statements = mScheme.onCreate();
            if (statements != null
                    && statements.size() > 0) {
                for (String statement: statements) {
                    db.execSQL(statement);
                }
            }
        } catch (StormSchemeException e) {
            throw StormException.newInstance(
                    e,
                    "Exception executing `OnCreate` statements"
            );
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            final List<String> statements = mScheme.onUpgrade(oldVersion, newVersion);
            if (statements != null
                    && statements.size() > 0) {
                for (String statement: statements) {
                    db.execSQL(statement);
                }
            }
        } catch (StormSchemeException e) {
            throw StormException.newInstance(
                    e,
                    "Exception executing `onUpgrade` statements"
            );
        }
    }
}
