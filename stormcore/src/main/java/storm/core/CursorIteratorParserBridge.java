package storm.core;

import android.database.Cursor;

import storm.iterator.CursorIteratorParser;
import storm.parser.converter.StormConverter;

/**
 * Created by Dimitry Ivanov on 16.12.2015.
 */
class CursorIteratorParserBridge<T extends StormObject> implements CursorIteratorParser<T> {

    final StormConverter<T> mConverter;

    CursorIteratorParserBridge(StormConverter<T> converter) {
        mConverter = converter;
    }

    @Override
    public T parse(Cursor cursor) {
        return mConverter.fromCursor(cursor);
    }
}
