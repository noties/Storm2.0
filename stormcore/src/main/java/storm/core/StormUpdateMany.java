package storm.core;

import java.util.Collection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public class StormUpdateMany<T extends StormObject> {

    private final Storm mStorm;
    private final Collection<T> mValues;
    private final StormUpdateManyDispatcher mUpdateManyDispatcher;

    StormUpdateMany(Storm storm, Collection<T> values, StormUpdateManyDispatcher updateManyDispatcher) {
        mStorm = storm;
        mValues = values;
        mUpdateManyDispatcher = updateManyDispatcher;
    }

    public int execute() {
        return mUpdateManyDispatcher.update(mStorm, mValues);
    }
}
