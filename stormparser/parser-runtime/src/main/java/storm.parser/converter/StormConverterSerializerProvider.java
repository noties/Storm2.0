package storm.parser.converter;

import storm.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public interface StormConverterSerializerProvider {
    <IN, OUT> StormSerializer<IN, OUT> provide(Class<IN> cl);
}
