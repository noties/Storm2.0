package storm.core;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import storm.query.Query;

/**
 * Created by Dimitry Ivanov on 20.12.2015.
 */
class StormSimpleQueryDispatcherImpl implements StormSimpleQueryDispatcher {

    @Override
    public int asInt(Storm storm, Query query, int defValue) {

        final Cursor cursor = getCursor(storm, query);
        if (cursor == null) {
            return defValue;
        }

        try {
            return cursor.getInt(0);
        } finally {
            cursor.close();
        }
    }

    @Override
    public long asLong(Storm storm, Query query, long defValue) {

        final Cursor cursor = getCursor(storm, query);
        if (cursor == null) {
            return defValue;
        }

        try {
            return cursor.getLong(0);
        } finally {
            cursor.close();
        }
    }

    @Override
    public float asFloat(Storm storm, Query query, float defValue) {

        final Cursor cursor = getCursor(storm, query);
        if (cursor == null) {
            return defValue;
        }

        try {
            return cursor.getFloat(0);
        } finally {
            cursor.close();
        }
    }

    @Override
    public double asDouble(Storm storm, Query query, double defValue) {

        final Cursor cursor = getCursor(storm, query);
        if (cursor == null) {
            return defValue;
        }

        try {
            return cursor.getDouble(0);
        } finally {
            cursor.close();
        }
    }

    @Override
    public String asString(Storm storm, Query query, String defValue) {

        final Cursor cursor = getCursor(storm, query);
        if (cursor == null) {
            return defValue;
        }

        try {
            return cursor.getString(0);
        } finally {
            cursor.close();
        }
    }

    @Override
    public byte[] asByteArray(Storm storm, Query query, byte[] defValue) {

        final Cursor cursor = getCursor(storm, query);
        if (cursor == null) {
            return defValue;
        }

        try {
            return cursor.getBlob(0);
        } finally {
            cursor.close();
        }

    }

    private static Cursor getCursor(Storm storm, Query query) {

        final SQLiteDatabase db = storm.database().open();
        boolean isSuccess = false;

        final Cursor cursor = db.rawQuery(query.getStatement(), query.getArguments());

        try {
            if (cursor == null) {
                return null;
            }

            if (!cursor.moveToFirst()
                    || cursor.getColumnCount() == 0) {
                cursor.close();
                return null;
            }

            isSuccess = true;
            cursor.registerDataSetObserver(new CloseDatabaseReferenceObserver(storm));
            return cursor;
        } finally {
            if (!isSuccess) {
                storm.database().close();
            }
        }
    }
}
