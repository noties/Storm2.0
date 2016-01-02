package storm.parser;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import storm.parser.converter.StormConverter;
import storm.parser.converter.StormConverterFactory;
import storm.parser.converter.StormConverterInstanceCreatorProvider;
import storm.parser.metadata.StormMetadata;
import storm.parser.metadata.StormMetadataFactory;
import storm.parser.scheme.StormScheme;
import storm.parser.scheme.StormSchemeFactory;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public class StormParserFactory {

    // stateless factories
    private static final StormSchemeFactory SCHEME_FACTORY = new StormSchemeFactory();
    private static final StormMetadataFactory METADATA_FACTORY = new StormMetadataFactory();

    private static final StormParserTableParser TABLE_PARSER = new StormParserTableParser();
    private static final StormParserHelperRuntime TABLE_PARSER_RUNTIME = new StormParserHelperRuntime();

    private final Map<Class<?>, StormParser<?>> mCache;
    private final StormConverterFactory mConverterFactory;

    public StormParserFactory(StormConverterInstanceCreatorProvider instanceCreatorProvider) {
        this.mCache = Collections.synchronizedMap(new HashMap<Class<?>, StormParser<?>>());
        this.mConverterFactory = new StormConverterFactory(instanceCreatorProvider, new StormConverterSerializerProviderImpl());
    }

    public <T> StormParser<T> provide(Class<T> cl) throws StormParserException {
        StormParser<?> parser = mCache.get(cl);
        if (parser == null) {
            parser = parser(cl);
            mCache.put(cl, parser);
        }
        //noinspection unchecked
        return (StormParser<T>) parser;
    }

    private synchronized <T> StormParser<T> parser(Class<T> cl) throws StormParserException {

        final Class<T> model = cl;

        // only runtime
        final ParserLazy<StormParserTable<Class<?>, Field, Class<?>>, StormParserException> table
                = new ParserLazy<>(new ParserLazy.Provider<StormParserTable<Class<?>, Field, Class<?>>, StormParserException>() {
            @Override
            public StormParserTable<Class<?>, Field, Class<?>> provide() throws StormParserException {
                return TABLE_PARSER.parseTable(TABLE_PARSER_RUNTIME, model);
            }
        });

        final ParserLazy<StormScheme, StormParserException> scheme;
        final ParserLazy<StormConverter<T>, StormParserException> converter;
        final ParserLazy<StormMetadata<T>, StormParserException> metadata;

        {
            scheme = new ParserLazy<>(new ParserLazy.Provider<StormScheme, StormParserException>() {
                @Override
                public StormScheme provide() throws StormParserException {
                    final StormScheme aptScheme = SCHEME_FACTORY.apt(model);
                    if (aptScheme != null) {
                        return aptScheme;
                    }

                    return SCHEME_FACTORY.runtime(table.get());
                }
            });
        }

        {
            converter = new ParserLazy<>(new ParserLazy.Provider<StormConverter<T>, StormParserException>() {
                @Override
                public StormConverter<T> provide() throws StormParserException {

                    final StormConverter<T> aptConverter = mConverterFactory.apt(model);

                    if (aptConverter != null) {
                        return aptConverter;
                    }

                    return mConverterFactory.runtime(table.get());
                }
            });
        }

        {
            metadata = new ParserLazy<>(new ParserLazy.Provider<StormMetadata<T>, StormParserException>() {
                @Override
                public StormMetadata<T> provide() throws StormParserException {

                    final StormMetadata<T> aptMetadata = METADATA_FACTORY.apt(model);
                    if (aptMetadata != null) {
                        return aptMetadata;
                    }

                    return METADATA_FACTORY.runtime(table.get());
                }
            });
        }

        return new StormParserImpl<>(scheme, converter, metadata);
    }
}
