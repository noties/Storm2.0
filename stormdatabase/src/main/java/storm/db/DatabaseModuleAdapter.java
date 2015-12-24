package storm.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
public abstract class DatabaseModuleAdapter implements DatabaseModule {
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {

    }
}
