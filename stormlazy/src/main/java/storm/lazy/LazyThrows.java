package storm.lazy;

/**
 * Created by Dimitry Ivanov on 24.01.2016.
 */
public class LazyThrows<V, T extends Throwable> {

    public interface LazyThrowsProvider<V, T extends Throwable> {
        V provide() throws T;
    }

    private final LazyThrowsProvider<V, T> mProvider;

    private V mCachedValue;
    private volatile boolean mIsProviderCalled;

    public LazyThrows(LazyThrowsProvider<V, T> provider) {
        this.mProvider = provider;
    }

    public synchronized V get() throws T {
        if (!mIsProviderCalled) {
            mCachedValue = mProvider.provide();
            mIsProviderCalled = true;
        }
        return mCachedValue;
    }

    public boolean isProviderCalled() {
        return mIsProviderCalled;
    }
}
