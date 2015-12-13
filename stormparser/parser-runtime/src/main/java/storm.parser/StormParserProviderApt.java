package storm.parser;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
class StormParserProviderApt implements StormParserProvider {

    static boolean lookup(Class<?> cl) {
        return false;
    }

    @Override
    public <T> StormParser<T> provideParser(Class<T> cl, StormInstanceCreator<T> instanceCreator, StormSerializerProvider serializerProvider) throws StormParserException {
        return null;
    }
}
