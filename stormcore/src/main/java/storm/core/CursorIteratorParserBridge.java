package storm.core;

import android.database.Cursor;

import storm.iterator.CursorIteratorParser;
import storm.parser.StormParser;

/**
 * Created by Dimitry Ivanov on 16.12.2015.
 */
class CursorIteratorParserBridge<T extends StormObject> implements CursorIteratorParser<T> {

    final StormParser<T> mParser;

    CursorIteratorParserBridge(StormParser<T> parser) {
        mParser = parser;
    }

    @Override
    public T parse(Cursor cursor) {
        return mParser.fromCursor(cursor);
    }
}
