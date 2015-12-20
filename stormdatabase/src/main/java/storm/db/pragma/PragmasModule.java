package storm.db.pragma;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import storm.db.DatabaseModuleConfiguration;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class PragmasModule extends DatabaseModuleConfiguration {

    private static final String PRAGMA_STATEMENT_PATTERN = "PRAGMA %1$s = \'%2$s\'";

    public static PragmasModule of(Pragma... pragmas) {
        return new PragmasModule(pragmas);
    }

    private final Pragma[] mPragmas;

    private PragmasModule(Pragma[] pragmas) {
        this.mPragmas = pragmas;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {

        Cursor cursor;

        for (Pragma pragma: mPragmas) {
            cursor = db.rawQuery(createPragmaStatement(pragma), null);
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    static String createPragmaStatement(Pragma pragma) {
        return String.format(PRAGMA_STATEMENT_PATTERN, pragma.getName(), pragma.getValue());
    }
}
