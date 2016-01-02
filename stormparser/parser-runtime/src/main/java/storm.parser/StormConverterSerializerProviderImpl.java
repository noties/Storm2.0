package storm.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import storm.parser.converter.StormConverterSerializerProvider;
import storm.reflect.ReflectionInstanceCreator;
import storm.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
class StormConverterSerializerProviderImpl implements StormConverterSerializerProvider {

    private final Map<Class<?>, StormSerializer<?, ?>> mSerializers;

    StormConverterSerializerProviderImpl() {
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
