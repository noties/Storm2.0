package storm.serializer;

/**
 * Created by Dimitry Ivanov on 12.12.2015.
 */
public interface StormSerializer<IN, OUT> {
    OUT serialize(IN in);
    IN deserialize(OUT out);
}
