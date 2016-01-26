package storm.parser.converter.serializer;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public interface StormSerializerProvider {
    <IN, OUT> StormSerializer<IN, OUT> provide(Class<IN> cl);
}
