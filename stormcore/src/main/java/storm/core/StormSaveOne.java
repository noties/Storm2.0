package storm.core;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public class StormSaveOne<T extends StormObject> {

    private final Storm mStorm;
    private final T mValue;
    private final StormSaveOneDispatcher mSaveDispatcher;

    StormSaveOne(Storm storm, T value, StormSaveOneDispatcher saveDispatcher) {
        mStorm = storm;
        mValue = value;
        mSaveDispatcher = saveDispatcher;
    }

    public long execute() {
        return mSaveDispatcher.save(mStorm, mValue);
    }
}
