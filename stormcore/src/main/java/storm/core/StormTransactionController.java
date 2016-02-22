package storm.core;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Dimitry Ivanov on 22.02.2016.
 */
class StormTransactionController {

    private final SQLiteDatabase mDatabase;
    private final boolean mIsInTransaction;

    StormTransactionController(SQLiteDatabase database) {
        mDatabase = database;
        mIsInTransaction = mDatabase.inTransaction();
    }

    void beginTransaction() {
        if (!mIsInTransaction) {
            mDatabase.beginTransaction();
        }
    }

    void setTransactionSuccessful() {
        if (!mIsInTransaction) {
            mDatabase.setTransactionSuccessful();
        }
    }

    void endTransaction() {
        if (!mIsInTransaction) {
            mDatabase.endTransaction();
        }
    }
}
