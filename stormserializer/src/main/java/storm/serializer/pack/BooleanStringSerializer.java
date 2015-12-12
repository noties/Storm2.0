package storm.serializer.pack;

import storm.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 12.12.2015.
 */
public class BooleanStringSerializer implements StormSerializer<Boolean, String> {
    @Override
    public String serialize(Boolean aBoolean) {
        return aBoolean != null && aBoolean ? "true" : "false";
    }

    @Override
    public Boolean deserialize(String s) {
        return s != null && s.equalsIgnoreCase("true") ? Boolean.TRUE : Boolean.FALSE;
    }
}
