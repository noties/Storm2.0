package storm.serializer.pack;

import storm.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 12.12.2015.
 */
public class BooleanIntSerializer implements StormSerializer<Boolean, Integer> {

    @Override
    public Integer serialize(Boolean aBoolean) {
        return aBoolean != null && aBoolean ? 1 : 0;
    }

    @Override
    public Boolean deserialize(Integer integer) {
        return integer != null && integer == 1;
    }
}
