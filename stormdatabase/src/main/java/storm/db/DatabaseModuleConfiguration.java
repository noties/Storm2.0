package storm.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public abstract class DatabaseModuleConfiguration implements DatabaseModule {
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public abstract void onOpen(SQLiteDatabase db);

    @Override
    public abstract void onConfigure(SQLiteDatabase db);

}
