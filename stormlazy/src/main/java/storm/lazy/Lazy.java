package storm.lazy;

public class Lazy<T> {

    public interface LazyProvider<T> {
        T provide();
    }

    private final LazyProvider<T> mProvider;

    private T mCachedValue;
    private volatile boolean mIsProviderCalled;

    public Lazy(LazyProvider<T> provider) {
        this.mProvider = provider;
    }

    public synchronized T get() {
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
