package storm.database;

import android.database.sqlite.SQLiteDatabase;

import storm.db.DatabaseModule;

/**
 * Created by Dimitry Ivanov on 20.12.2015.
 */
class AllMethodsModule implements DatabaseModule {

    boolean isOnCreateCalled;
    boolean isOnUpgradeCalled;
    boolean isOnOpenCalled;
    boolean isOnConfigureCalled;

    @Override
    public void onCreate(SQLiteDatabase db) {
        isOnCreateCalled = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        isOnUpgradeCalled = true;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        isOnOpenCalled = true;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        isOnConfigureCalled = true;
    }

    @Override
    public String toString() {
        return "AllMethodsModule{" +
                "isOnCreateCalled=" + isOnCreateCalled +
                ", isOnUpgradeCalled=" + isOnUpgradeCalled +
                ", isOnOpenCalled=" + isOnOpenCalled +
                ", isOnConfigureCalled=" + isOnConfigureCalled +
                '}';
    }
}
