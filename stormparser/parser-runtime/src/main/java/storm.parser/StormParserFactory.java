package storm.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import storm.lazy.Lazy;
import storm.parser.converter.StormConverter;
import storm.parser.converter.StormConverterProviderFactory;
import storm.parser.metadata.StormMetadata;
import storm.parser.scheme.StormScheme;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public class StormParserFactory {

    private static final StormConverterProviderFactory CONVERTER_FACTORY = new StormConverterProviderFactory();

    private final Map<Class<?>, StormParser<?>> mCache;

    public StormParserFactory() {
        this.mCache = Collections.synchronizedMap(new HashMap<Class<?>, StormParser<?>>());
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

        final Lazy<StormParserTable> table = new Lazy<>(new Lazy.LazyProvider<StormParserTable>() {
            @Override
            public StormParserTable provide() {
                return null;
            }
        });

        final Lazy<StormScheme> scheme;
        final Lazy<StormConverter<T>> converter;
        final Lazy<StormMetadata<T>> metadata;

        {
            scheme = null;
            metadata = null;
        }

        {
            converter = new Lazy<>(new Lazy.LazyProvider<StormConverter<T>>() {
                @Override
                public StormConverter<T> provide() {

                    final StormConverter<T> aptConverter = CONVERTER_FACTORY.apt(model);

                    if (aptConverter != null) {
                        return aptConverter;
                    }

                    try {
                        return CONVERTER_FACTORY.runtime(table.get());
                    } catch (StormParserException e) {
                        StormParserFactory.<RuntimeException>throwException(e);
                    }

                    return null;
                }
            });
        }

        return new StormParserImpl<>(scheme, converter, metadata);
    }

    static <T extends Throwable> void throwException(Throwable ex) throws T {
        //noinspection unchecked
        throw (T) ex;
    }
}
