package storm.core;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public class StormUpdateOne<T extends StormObject> implements StormOp<T> {

    private final Storm mStorm;
    private final T mValue;
    private final StormUpdateOneDispatcher mUpdateOneDispatcher;

    protected StormUpdateOne(Storm storm, T value, StormUpdateOneDispatcher updateOneDispatcher) {
        mStorm = storm;
        mValue = value;
        mUpdateOneDispatcher = updateOneDispatcher;
    }

    public int execute() {
        return mUpdateOneDispatcher.update(mStorm, mValue);
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

    @Override
    public StormUpdateOneDispatcher dispatcher() {
        return mUpdateOneDispatcher;
    }

    public T value() {
        return mValue;
    }
}
