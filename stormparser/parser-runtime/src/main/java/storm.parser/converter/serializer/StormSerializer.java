package storm.parser.converter.serializer;

/**
 * Created by Dimitry Ivanov on 25.01.2016.
 */
public interface StormSerializer<IN, OUT> {
    OUT serialize(IN in);
    IN deserialize(OUT out);
}