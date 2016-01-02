package storm.parser;

import storm.reflect.ReflectionInstanceCreator;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
class StormParserProviderApt implements StormParserProvider {

    static boolean lookup(Class<?> cl) {
        try {
            createAptClass(cl);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public <T> StormParser<T> provideParser(Class<T> cl, StormInstanceCreator<T> instanceCreator, StormSerializerProvider serializerProvider) throws StormParserException {

        Class<?> aptParser = null;

        try {
            aptParser = createAptClass(cl);
        } catch (ClassNotFoundException e) {
            throw new StormParserException(e);
        }

        //noinspection unchecked
        return (StormParser<T>) ReflectionInstanceCreator.newInstance(aptParser);
    }

    static Class<?> createAptClass(Class<?> cl) throws ClassNotFoundException {
        return Class.forName(StormParserAptClassNameBuilder2.parserFullName(
                cl.getPackage().getName(),
                cl.getSimpleName()
        ));
    }
}
