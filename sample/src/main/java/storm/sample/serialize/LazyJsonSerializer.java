package storm.sample.serialize;

import java.lang.reflect.Type;

import storm.lazy.Lazy;
import storm.parser.converter.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 13.02.2016.
 */
class LazyJsonSerializer implements StormSerializer<Lazy<SerializeJsonItem>, String> {

    private static final JsonSerializer SERIALIZER = new JsonSerializer();

    @Override
    public String serialize(Lazy<SerializeJsonItem> serializeJsonItemLazy) {
        return serializeJsonItemLazy == null ? null : SERIALIZER.serialize(serializeJsonItemLazy.get());
    }

    @Override
    public Lazy<SerializeJsonItem> deserialize(final Type type, final String s) {
        return s == null || s.length() == 0 ? null : new Lazy<>(new Lazy.LazyProvider<SerializeJsonItem>() {
            @Override
            public SerializeJsonItem provide() {
                return SERIALIZER.deserialize(type, s);
            }
        });
    }
}
