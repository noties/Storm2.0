package storm.parser;

import java.lang.reflect.Field;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
class StormParserProviderRuntime implements StormParserProvider {

    private static final StormParserTypeRuntime TYPE_RUNTIME = new StormParserTypeRuntime();
    private static final StormParserGenericParser GENERIC_PARSER = new StormParserGenericParser();

    @Override
    public <T> StormParser<T> provideParser(
            Class<T> cl,
            StormInstanceCreator<T> instanceCreator,
            StormSerializerProvider serializerProvider
    ) throws StormParserException {

        final StormParserTable<Field, Class<?>> table = GENERIC_PARSER.parseTable(TYPE_RUNTIME, cl);

        return new StormParserRuntime<>(
                cl,
                table,
                instanceCreator,
                serializerProvider
        );
    }
}
