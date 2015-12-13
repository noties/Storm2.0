package storm.parser;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
interface StormParserProvider {

    <T> StormParser<T> provideParser(
            Class<T> cl,
            StormInstanceCreator<T> instanceCreator,
            StormSerializerProvider serializerProvider
    ) throws StormParserException;

}
