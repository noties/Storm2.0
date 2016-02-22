package storm.sample.serialize;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import storm.parser.converter.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 16.02.2016.
 */
class GenericJsonSerializer<T> implements StormSerializer<T, String> {

    @Override
    public String serialize(T o) {
        return new Gson().toJson(o);
    }

    @Override
    public T deserialize(Type type, String s) {
        return new Gson().fromJson(s, type);
    }
}
