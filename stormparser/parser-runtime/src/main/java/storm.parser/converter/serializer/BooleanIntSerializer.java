package storm.parser.converter.serializer;

import java.lang.reflect.Type;

/**
 * Created by Dimitry Ivanov on 12.12.2015.
 */
public class BooleanIntSerializer implements StormSerializer<Boolean, Integer> {

    @Override
    public Integer serialize(Boolean aBoolean) {
        return aBoolean != null && aBoolean ? 1 : 0;
    }

    @Override
    public Boolean deserialize(Type type, Integer integer) {
        return integer != null && integer == 1;
    }
}
