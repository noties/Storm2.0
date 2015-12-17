package storm.core;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public class StormUpdateOne<T extends StormObject> {

    private final Storm mStorm;
    private final T mValue;
    private final StormUpdateOneDispatcher mUpdateOneDispatcher;

    StormUpdateOne(Storm storm, T value, StormUpdateOneDispatcher updateOneDispatcher) {
        mStorm = storm;
        mValue = value;
        mUpdateOneDispatcher = updateOneDispatcher;
    }

    public int execute() {
        return mUpdateOneDispatcher.update(mStorm, mValue);
    }
}
