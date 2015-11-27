package storm.iterator;

import android.database.Cursor;
import android.database.DataSetObserver;

import java.util.Iterator;

/**
 * Created by Dimitry Ivanov on 19.09.2015.
 */
public abstract class CursorIteratorBase<T> implements CursorIterator<T> {

    final Cursor mCursor;

    private int mPosition = -1;

    public CursorIteratorBase(Cursor cursor) {
        this.mCursor = cursor;
        if (mCursor != null) {
            mCursor.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onInvalidated() {
                    mCursor.unregisterDataSetObserver(this);
                    close();
                }
            });
        }
    }

    @Override
    public int getCount() {
        if (isClosed()) {
            return 0;
        }
        return mCursor.getCount();
    }

    @Override
    public void close() {
        if (!isClosed()) {
            mCursor.close();
        }
    }

    @Override
    public boolean isClosed() {
        return mCursor == null || mCursor.isClosed();
    }

    @Override
    public T get(int position) {

        if (isClosed()) {
            throw new IllegalStateException("Calling `StormIteratorBase.get(int)`, but cursor is NULL or already closed");
        }

        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Calling `StormIteratorBase.get(int)`, but cursor doesn't have such position: " + position);
        }

        return getAfterCursorChecked(position);
    }

    protected abstract T getAfterCursorChecked(int position);

    @Override
    public boolean hasNext() {
        return (mPosition + 1) < getCount();
    }

    @Override
    public T next() {
        return get(++mPosition);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    public Cursor getCursor() {
        return mCursor;
    }
}
