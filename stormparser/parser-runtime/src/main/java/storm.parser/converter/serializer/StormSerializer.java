package storm.parser.converter.serializer;

import java.lang.reflect.Type;

/**
 * Created by Dimitry Ivanov on 25.01.2016.
 */
public interface StormSerializer<IN, OUT> {
    OUT serialize(IN in);
    IN deserialize(Type type, OUT out);
}