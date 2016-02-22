package storm.sample.serialize;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import storm.lazy.Lazy;
import storm.parser.converter.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 21.02.2016.
 */
public class GenericLazyJsonSerializer<T> implements StormSerializer<Lazy<T>, String> {

    private static final Gson GSON = new Gson();

    @Override
    public String serialize(Lazy<T> lazy) {
        return lazy == null ? null : GSON.toJson(lazy.get());
    }

    @Override
    public Lazy<T> deserialize(final Type type, final String s) {
        return s == null || s.length() == 0 ? null : new Lazy<>(new Lazy.LazyProvider<T>() {
            @Override
            public T provide() {
                // because type is Lazy<T> now, we need first type argument
                return GSON.fromJson(s, ((ParameterizedType) type).getActualTypeArguments()[0]);
            }
        });
    }
}
