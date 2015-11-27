package storm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by Dimitry Ivanov on 19.09.2015.
 */
class DatabaseOpenHelper extends SQLiteOpenHelper {

    private List<DatabaseModule> mModules;

    DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    void setModules(List<DatabaseModule> modules) {
        this.mModules = modules;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (mModules != null) {
            for (DatabaseModule module: mModules) {
                module.onCreate(db);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (mModules != null) {
            for (DatabaseModule module: mModules) {
                module.onUpgrade(db, oldVersion, newVersion);
            }
        }
    }
}
