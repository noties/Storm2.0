package storm.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import storm.reflect.ReflectionInstanceCreator;
import storm.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class StormParserFactory2 {

    public interface InstanceCreatorProvider {
        <T> StormInstanceCreator<T> provide(Class<T> cl);
    }

    private static final StormParserProvider PROVIDER_APT = new StormParserProviderApt();
    private static final StormParserProvider PROVIDER_RUNTIME = new StormParserProviderRuntime();

    private final InstanceCreatorProvider mInstanceCreatorProvider;
    private final Map<Class<?>, StormParser<?>> mCache;
    private final StormSerializerProvider mSerializerProvider;

    public StormParserFactory2(InstanceCreatorProvider instanceCreatorProvider) {
        this.mInstanceCreatorProvider = instanceCreatorProvider;
        this.mCache = Collections.synchronizedMap(new HashMap<Class<?>, StormParser<?>>());
        this.mSerializerProvider = new StormSerializerProviderImpl();
    }

    public <T> StormParser<T> provide(Class<T> cl) throws StormParserException {
        StormParser<?> parser = mCache.get(cl);
        if (parser == null) {
            synchronized (this) {
                final StormInstanceCreator<T> instanceCreator = mInstanceCreatorProvider.provide(cl);
                if (StormParserProviderApt.lookup(cl)) {
                    parser = PROVIDER_APT.provideParser(cl, instanceCreator, mSerializerProvider);
                } else {
                    parser = PROVIDER_RUNTIME.provideParser(cl, instanceCreator, mSerializerProvider);
                }
                mCache.put(cl, parser);
            }
        }
        //noinspection unchecked
        return (StormParser<T>) parser;
    }

    private static class StormSerializerProviderImpl implements StormSerializerProvider {

        private final Map<Class<?>, StormSerializer<?, ?>> mSerializers;

        StormSerializerProviderImpl() {
            this.mSerializers = Collections.synchronizedMap(new HashMap<Class<?>, StormSerializer<?, ?>>());
        }

        @Override
        public <IN, OUT> StormSerializer<IN, OUT> provide(Class<IN> cl) {
            StormSerializer<?, ?> serializer = mSerializers.get(cl);
            if (serializer == null) {
                // lookup
                serializer = (StormSerializer<?, ?>) ReflectionInstanceCreator.newInstance(cl);
                mSerializers.put(cl, serializer);
            }
            //noinspection unchecked
            return (StormSerializer<IN, OUT>) serializer;
        }
    }
}
