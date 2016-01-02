package storm.parser;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
class ParserLazy<V, T extends Throwable> {

    interface Provider<V, T extends Throwable> {
        V provide() throws T;
    }

    private final Provider<V, T> mProvider;

    private V mValue;
    private volatile boolean mIsProviderCalled;

    ParserLazy(Provider<V, T> provider) {
        this.mProvider = provider;
    }

    public synchronized V get() throws T {
        if (!mIsProviderCalled) {
            mValue = mProvider.provide();
            mIsProviderCalled = true;
        }
        return mValue;
    }
}
