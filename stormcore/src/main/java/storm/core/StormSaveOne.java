package storm.core;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public class StormSaveOne<T extends StormObject> implements StormOp<T> {

    private final Storm mStorm;
    private final T mValue;
    private final StormSaveOneDispatcher mSaveDispatcher;

    protected StormSaveOne(Storm storm, T value, StormSaveOneDispatcher saveDispatcher) {
        mStorm = storm;
        mValue = value;
        mSaveDispatcher = saveDispatcher;
    }

    public long execute() {
        return mSaveDispatcher.save(mStorm, mValue);
    }

    @Override
    public Storm storm() {
        return mStorm;
    }

    @Override
    public Class<T> table() {
        //noinspection unchecked
        return (Class<T>) mValue.getClass();
    }

    public T value() {
        return mValue;
    }

    @Override
    public StormSaveOneDispatcher dispatcher() {
        return mSaveDispatcher;
    }
}
