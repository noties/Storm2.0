package storm.core;

import java.util.Collection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public class StormSaveMany<T extends StormObject> {

    private final Storm mStorm;
    private final Collection<T> mValues;
    private final StormSaveManyDispatcher mSaveManyDispatcher;

    StormSaveMany(Storm storm, Collection<T> values, StormSaveManyDispatcher saveManyDispatcher) {
        mStorm = storm;
        mValues = values;
        mSaveManyDispatcher = saveManyDispatcher;
    }

    public long[] execute() {
        return mSaveManyDispatcher.save(mStorm, mValues);
    }
}
