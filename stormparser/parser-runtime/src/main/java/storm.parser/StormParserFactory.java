package storm.parser;

import java.util.HashMap;
import java.util.Map;

import storm.parser.converter.StormConverter;
import storm.parser.converter.StormConverterAptClassNameBuilder;
import storm.parser.converter.StormConverterInstanceCreatorProvider;
import storm.parser.converter.serializer.StormSerializer;
import storm.parser.converter.serializer.StormSerializerProvider;
import storm.parser.metadata.StormMetadata;
import storm.parser.metadata.StormMetadataAptClassNameBuilder;
import storm.parser.scheme.StormScheme;
import storm.parser.scheme.StormSchemeAptNameBuilder;
import storm.reflect.ReflectionInstanceCreator;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public class StormParserFactory {

    private static final StormParserItemFactory FACTORY = new StormParserItemFactory();

    private final StormConverterInstanceCreatorProvider mInstanceCreatorProvider;
    private final StormSerializerProvider mSerializerProvider;

    private final Map<Class<?>, StormParser<?>> mCachedParsers;

    public StormParserFactory(StormConverterInstanceCreatorProvider instanceCreatorProvider) {
        this.mInstanceCreatorProvider = instanceCreatorProvider;
        this.mSerializerProvider = new ParserSerializerProvider();
        this.mCachedParsers = new HashMap<>();
    }

    public synchronized <T> StormParser<T> provide(Class<T> cl) throws StormParserException {
        StormParser<?> parser = mCachedParsers.get(cl);
        if (parser == null) {
            parser = parser(cl);
            mCachedParsers.put(cl, parser);
        }
        //noinspection unchecked
        return (StormParser<T>) parser;
    }

    private <T> StormParser<T> parser(final Class<T> cl) {

        final ParserLazy<StormScheme> scheme = new ParserLazy<>(new ParserLazy.ParserLazyProvider<StormScheme>() {
            @Override
            public StormScheme provide() throws StormParserException {
                return FACTORY.provide(StormSchemeAptNameBuilder.getInstance(), cl);
            }
        });

        final ParserLazy<StormConverter<T>> converter = new ParserLazy<>(new ParserLazy.ParserLazyProvider<StormConverter<T>>() {
            @Override
            public StormConverter<T> provide() throws StormParserException {
                return FACTORY.provide(StormConverterAptClassNameBuilder.getInstance(), cl, mInstanceCreatorProvider, mSerializerProvider);
            }
        });

        final ParserLazy<StormMetadata<T>> metadata = new ParserLazy<>(new ParserLazy.ParserLazyProvider<StormMetadata<T>>() {
            @Override
            public StormMetadata<T> provide() throws StormParserException {
                return FACTORY.provide(StormMetadataAptClassNameBuilder.getInstance(), cl);
            }
        });

        return new StormParserImpl<>(scheme, converter, metadata);
    }

    private static class ParserSerializerProvider implements StormSerializerProvider {

        private final Map<Class<?>, StormSerializer> mCache;

        ParserSerializerProvider() {
            this.mCache = new HashMap<>();
        }

        @Override
        public synchronized <IN, OUT> StormSerializer<IN, OUT> provide(Class<IN> cl) {
            StormSerializer<?, ?> serializer = mCache.get(cl);
            if (serializer == null) {
                serializer = (StormSerializer<?, ?>) ReflectionInstanceCreator.newInstance(cl);
                mCache.put(cl, serializer);
            }
            //noinspection unchecked
            return (StormSerializer<IN, OUT>) serializer;
        }
    }
}
