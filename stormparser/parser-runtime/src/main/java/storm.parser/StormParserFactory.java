package storm.parser;

import java.util.HashMap;
import java.util.Map;

import storm.parser.converter.StormConverter;
import storm.parser.converter.StormConverterAptClassNameBuilder;
import storm.parser.metadata.StormMetadata;
import storm.parser.metadata.StormMetadataAptClassNameBuilder;
import storm.parser.scheme.StormScheme;
import storm.parser.scheme.StormSchemeAptNameBuilder;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public class StormParserFactory {

    private static final StormParserItemFactory FACTORY = new StormParserItemFactory();

    private final Map<Class<?>, StormParser<?>> mCachedParsers;

    public StormParserFactory() {
        this.mCachedParsers = new HashMap<>();
    }

    public synchronized <T> StormParser<T> provide(Class<T> cl) {
        StormParser<?> parser = mCachedParsers.get(cl);
        if (parser == null) {
            parser = parser(cl);
            mCachedParsers.put(cl, parser);
        }
        //noinspection unchecked
        return (StormParser<T>) parser;
    }

    private synchronized <T> StormParser<T> parser(final Class<T> cl) {

        final ParserLazy<StormScheme> scheme = new ParserLazy<>(new ParserLazy.ParserLazyProvider<StormScheme>() {
            @Override
            public StormScheme provide() throws StormParserException {
                final StormScheme stormScheme = FACTORY.provide(StormSchemeAptNameBuilder.getInstance(), cl);
                if (stormScheme == null) {
                    throw StormParserException.newInstance("Unable to obtain StormScheme generated class for a class: " + cl.getName());
                }
                return stormScheme;
            }
        });

        final ParserLazy<StormConverter<T>> converter = new ParserLazy<>(new ParserLazy.ParserLazyProvider<StormConverter<T>>() {
            @Override
            public StormConverter<T> provide() throws StormParserException {
                final StormConverter<T> stormConverter = FACTORY.provide(StormConverterAptClassNameBuilder.getInstance(), cl);
                if (stormConverter == null) {
                    throw StormParserException.newInstance("Unable to obtain StormConverter generated class for a class: " + cl.getName());
                }
                return stormConverter;
            }
        });

        final ParserLazy<StormMetadata<T>> metadata = new ParserLazy<>(new ParserLazy.ParserLazyProvider<StormMetadata<T>>() {
            @Override
            public StormMetadata<T> provide() throws StormParserException {
                final StormMetadata<T> stormMetadata = FACTORY.provide(StormMetadataAptClassNameBuilder.getInstance(), cl);
                if (stormMetadata == null) {
                    throw StormParserException.newInstance("Unable to obtain StormMetadata generated class for a class: " + cl.getName());
                }
                return stormMetadata;
            }
        });

        return new StormParserImpl<>(scheme, converter, metadata);
    }
}
