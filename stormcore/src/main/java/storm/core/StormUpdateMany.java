package storm.core;

import java.util.Collection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public class StormUpdateMany<T extends StormObject> implements StormOp<T> {

    private final Storm mStorm;
    private final Collection<T> mValues;
    private final StormUpdateManyDispatcher mUpdateManyDispatcher;

    protected StormUpdateMany(Storm storm, Collection<T> values, StormUpdateManyDispatcher updateManyDispatcher) {
        mStorm = storm;
        mValues = values;
        mUpdateManyDispatcher = updateManyDispatcher;
    }

    public int execute() {
        return mUpdateManyDispatcher.update(mStorm, mValues);
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
    public StormUpdateManyDispatcher dispatcher() {
        return mUpdateManyDispatcher;
    }

    public Collection<T> values() {
        return mValues;
    }
}
