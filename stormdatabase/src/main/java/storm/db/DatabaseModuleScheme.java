package storm.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public abstract class DatabaseModuleScheme implements DatabaseModule {

    @Override
    public abstract void onCreate(SQLiteDatabase db);

    @Override
    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    @Override
    public void onOpen(SQLiteDatabase db) {

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {

    }
}
