package storm.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import storm.lazy.Lazy;
import storm.parser.converter.StormConverterInstanceCreator;

/**
 * Created by Dimitry Ivanov on 16.12.2015.
 */
class StormInstanceCreators {

    private final Lazy<Map<Class<?>, StormConverterInstanceCreator<?>>> mCache = new Lazy<>(new Lazy.LazyProvider<Map<Class<?>, StormConverterInstanceCreator<?>>>() {
        @Override
        public Map<Class<?>, StormConverterInstanceCreator<?>> provide() {
            return Collections.synchronizedMap(new HashMap<Class<?>, StormConverterInstanceCreator<?>>());
        }
    });

    StormInstanceCreators() {

    }

    <T extends StormObject> StormConverterInstanceCreator<T> get(Class<T> table) {
        StormConverterInstanceCreator<?> creator = mCache.get().get(table);
        if (creator == null) {
            creator = new StormReflectionInstanceCreator<>(table);
            mCache.get().put(table, creator);
        }
        //noinspection unchecked
        return (StormConverterInstanceCreator<T>) creator;
    }

    <T extends StormObject> void put(Class<T> table, StormConverterInstanceCreator<T> creator) {
        mCache.get().put(table, creator);
    }
}
