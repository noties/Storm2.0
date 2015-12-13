package storm.parser;

import storm.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
interface StormSerializerProvider {
    <IN, OUT> StormSerializer<IN, OUT> provide(Class<IN> cl);
}
