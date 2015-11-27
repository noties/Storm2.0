package storm.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Dimitry Ivanov on 19.09.2015.
 */
public interface DatabaseModule {

    void onCreate(SQLiteDatabase db);
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

}
