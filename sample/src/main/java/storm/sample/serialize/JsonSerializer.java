package storm.sample.serialize;

import com.google.gson.Gson;

import storm.parser.converter.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 13.02.2016.
 */
public class JsonSerializer implements StormSerializer<SerializeJsonItem, String> {

    private static final Gson GSON = new Gson();

    @Override
    public String serialize(SerializeJsonItem serializeJsonItem) {
        return serializeJsonItem == null ? null : GSON.toJson(serializeJsonItem);
    }

    @Override
    public SerializeJsonItem deserialize(String s) {
        return s == null || s.length() == 0 ? null : GSON.fromJson(s, SerializeJsonItem.class);
    }
}
