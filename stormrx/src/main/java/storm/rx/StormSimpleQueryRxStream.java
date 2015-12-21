package storm.rx;

import rx.Observable;
import storm.core.StormObject;
import storm.core.StormSimpleQueryDispatcher;
import storm.query.Query;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormSimpleQueryRxStream<T extends StormObject> implements StormRxStreamWithUpdates {

    private final StormRx mStorm;
    private final Class<T> mTable;
    private final Query mQuery;
    private final StormSimpleQueryDispatcher mDispatcher;

    private boolean mOneShot;

    StormSimpleQueryRxStream(StormSimpleQueryRx<T> query) {
        mStorm = query.storm();
        mTable = query.table();
        mQuery = query.query();
        mDispatcher = query.dispatcher();
        mOneShot = true;
    }

    @Override
    public StormSimpleQueryRxStream<T> subscribeForUpdates() {
        mOneShot = false;
        return this;
    }

    public Observable<Integer> asInt() {
        return asInt(0);
    }

    public Observable<Integer> asInt(final int defValue) {

        final StormRxObservable.ValueProvider<Integer> provider = new StormRxObservable.ValueProvider<Integer>() {
            @Override
            public Integer provide() {
                return mDispatcher.asInt(mStorm, mQuery, defValue);
            }
        };

        if (mOneShot) {
            return StormRxObservable.createOneShot(mStorm, provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }

    public Observable<Float> asFloat() {
        return asFloat(.0F);
    }

    public Observable<Float> asFloat(final float defValue) {

        final StormRxObservable.ValueProvider<Float> provider = new StormRxObservable.ValueProvider<Float>() {
            @Override
            public Float provide() {
                return mDispatcher.asFloat(mStorm, mQuery, defValue);
            }
        };

        if (mOneShot) {
            return StormRxObservable.createOneShot(mStorm, provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }

    public Observable<Double> asDouble() {
        return asDouble(.0D);
    }

    public Observable<Double> asDouble(final double defValue) {

        final StormRxObservable.ValueProvider<Double> provider = new StormRxObservable.ValueProvider<Double>() {
            @Override
            public Double provide() {
                return mDispatcher.asDouble(mStorm, mQuery, defValue);
            }
        };

        if (mOneShot) {
            return StormRxObservable.createOneShot(mStorm, provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }

    public Observable<Long> asLong() {
        return asLong(0L);
    }

    public Observable<Long> asLong(final long defValue) {

        final StormRxObservable.ValueProvider<Long> provider = new StormRxObservable.ValueProvider<Long>() {
            @Override
            public Long provide() {
                return mDispatcher.asLong(mStorm, mQuery, defValue);
            }
        };

        if (mOneShot) {
            return StormRxObservable.createOneShot(mStorm, provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }

    public Observable<byte[]> asByteArray() {
        return asByteArray(null);
    }

    public Observable<byte[]> asByteArray(final byte[] defValue) {

        final StormRxObservable.ValueProvider<byte[]> provider = new StormRxObservable.ValueProvider<byte[]>() {
            @Override
            public byte[] provide() {
                return mDispatcher.asByteArray(mStorm, mQuery, defValue);
            }
        };

        if (mOneShot) {
            return StormRxObservable.createOneShot(mStorm, provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }

    public Observable<String> asString() {
        return asString(null);
    }

    public Observable<String> asString(final String defValue) {

        final StormRxObservable.ValueProvider<String> provider = new StormRxObservable.ValueProvider<String>() {
            @Override
            public String provide() {
                return mDispatcher.asString(mStorm, mQuery, defValue);
            }
        };

        if (mOneShot) {
            return StormRxObservable.createOneShot(mStorm, provider);
        }

        return StormRxObservable.createStream(mStorm, mTable, provider);
    }
}
