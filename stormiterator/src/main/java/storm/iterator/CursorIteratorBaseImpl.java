package storm.iterator;

import android.database.Cursor;

/**
 * Created by Dimitry Ivanov on 19.09.2015.
 */
public class CursorIteratorBaseImpl<T> extends CursorIteratorBase<T> {

    private final CursorIteratorParser<T> mCursorIteratorParser;

    public CursorIteratorBaseImpl(Cursor cursor, CursorIteratorParser<T> cursorIteratorParser) {
        super(cursor);
        this.mCursorIteratorParser = cursorIteratorParser;
    }

    @Override
    protected T getAfterCursorChecked(int position) {
        return mCursorIteratorParser.parse(mCursor);
    }
}
