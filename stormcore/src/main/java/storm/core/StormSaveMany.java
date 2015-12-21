package storm.core;

import java.util.Collection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public class StormSaveMany<T extends StormObject> implements StormOp<T> {

    private final Storm mStorm;
    private final Collection<T> mValues;
    private final StormSaveManyDispatcher mSaveManyDispatcher;

    protected StormSaveMany(Storm storm, Collection<T> values, StormSaveManyDispatcher saveManyDispatcher) {
        mStorm = storm;
        mValues = values;
        mSaveManyDispatcher = saveManyDispatcher;
    }

    public long[] execute() {
        return mSaveManyDispatcher.save(mStorm, mValues);
    }

    @Override
    public Storm storm() {
        return mStorm;
    }

    @Override
    public Class<T> table() {
        //noinspection unchecked
        return (Class<T>) mValues.iterator().next().getClass();
    }

    @Override
    public StormSaveManyDispatcher dispatcher() {
        return mSaveManyDispatcher;
    }

    public Collection<T> values() {
        return mValues;
    }
}
