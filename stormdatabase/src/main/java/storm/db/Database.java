package storm.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dimitry Ivanov on 19.09.2015.
 */
public class Database implements Closeable {

    public static class Configuration {

        // required
        final Context context;
        final String dbName;
        final int dbVersion;

        // optional
        SQLiteDatabase.CursorFactory cursorFactory;

        public Configuration(Context context, String dbName, int dbVersion) {
            this.context = context;
            this.dbName = dbName;
            this.dbVersion = dbVersion;
        }

        public Configuration setCursorFactory(SQLiteDatabase.CursorFactory cursorFactory) {
            this.cursorFactory = cursorFactory;
            return this;
        }
    }

    private final DatabaseOpenHelper mOpenHelper;
    private final ContentResolver mContentResolver;
    private final List<DatabaseModule> mModules;
    private final Object mMutex;

    private SQLiteDatabase mDatabase;
    private int mOpenCount;

    public Database(Configuration configuration) {
        final Context context = configuration.context.getApplicationContext();
        this.mOpenHelper = new DatabaseOpenHelper(
                context,
                configuration.dbName,
                configuration.cursorFactory,
                configuration.dbVersion
        );
        this.mContentResolver = context.getContentResolver();
        this.mModules = new ArrayList<>();
        this.mMutex = new Object();
    }

    // any modules must be registered before calling open
    public void registerModule(DatabaseModule dbModule) {
        synchronized (mMutex) {
            mModules.add(dbModule);
        }
    }

    public void registerModules(DatabaseModule... modules) {
        synchronized (mMutex) {
            Collections.addAll(mModules, modules);
        }
    }

    public void registerModules(Collection<DatabaseModule> modules) {
        synchronized (mMutex) {
            mModules.addAll(modules);
        }
    }

    // after obtaining SQLiteDatabase through this call, don't forget to call `Database.close()`
    // the best practice would be try/finally block.
    // don't call `close()` on a SQLiteDatabase object instance
    public SQLiteDatabase open() {

        synchronized (mMutex) {
            if (++mOpenCount == 1) {
                final List<DatabaseModule> modules = mModules;
                if (modules.size() == 0) {
                    // indicate that no modules were supplied
                    throw new IllegalStateException("Called `open` but Database has no registered modules. Did you call `Database.registerModule(s)`?");
                } else {
                    mOpenHelper.setModules(modules);
                }
                mDatabase = mOpenHelper.getWritableDatabase();
            }
        }

        if (!mDatabase.isOpen()) {
            throw new IllegalStateException("SQLiteDatabase is closed, did you call `SQLiteDatabase.close()` instead of `Database.close()`");
        }

        return mDatabase;
    }

    @Override
    public void close() {

        synchronized (mMutex) {
            if (--mOpenCount == 0) {
                mDatabase.close();
                mDatabase = null;
            }
        }
    }

    public void notify(Uri uri) {
        mContentResolver.notifyChange(uri, null, false);
    }

    public void registerContentObserver(Uri uri, ContentObserver contentObserver) {
        mContentResolver.registerContentObserver(uri, false, contentObserver);
    }

    public void unregisterContentObserver(ContentObserver contentObserver) {
        mContentResolver.unregisterContentObserver(contentObserver);
    }
}
