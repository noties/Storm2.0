package storm.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import storm.lazy.Lazy;
import storm.parser.StormInstanceCreator;

/**
 * Created by Dimitry Ivanov on 16.12.2015.
 */
class StormInstanceCreators {

    private final Lazy<Map<Class<?>, StormInstanceCreator<?>>> mCache = new Lazy<>(new Lazy.LazyProvider<Map<Class<?>, StormInstanceCreator<?>>>() {
        @Override
        public Map<Class<?>, StormInstanceCreator<?>> provide() {
            return Collections.synchronizedMap(new HashMap<Class<?>, StormInstanceCreator<?>>());
        }
    });

    StormInstanceCreators() {

    }

    <T extends StormObject> StormInstanceCreator<T> get(Class<T> table) {
        StormInstanceCreator<?> creator = mCache.get().get(table);
        if (creator == null) {
            creator = new ReflectionInstanceCreator<>(table);
            mCache.get().put(table, creator);
        }
        //noinspection unchecked
        return (StormInstanceCreator<T>) creator;
    }

    <T extends StormObject> void put(Class<T> table, StormInstanceCreator<T> creator) {
        mCache.get().put(table, creator);
    }
}
