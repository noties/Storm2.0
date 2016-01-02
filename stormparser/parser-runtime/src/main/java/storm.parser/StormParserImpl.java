package storm.parser;

import storm.lazy.Lazy;
import storm.parser.converter.StormConverter;
import storm.parser.converter.StormConverterException;
import storm.parser.metadata.StormMetadata;
import storm.parser.scheme.StormScheme;
import storm.parser.scheme.StormSchemeException;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
class StormParserImpl<T> implements StormParser<T> {

    private final Lazy<StormScheme> mSchemeLazy;
    private final Lazy<StormConverter<T>> mConverterLazy;
    private final Lazy<StormMetadata<T>> mMetadataLazy;

    StormParserImpl(Lazy<StormScheme> schemeLazy, Lazy<StormConverter<T>> converterLazy, Lazy<StormMetadata<T>> metadataLazy) {
        mSchemeLazy = schemeLazy;
        mConverterLazy = converterLazy;
        mMetadataLazy = metadataLazy;
    }

    @Override
    public StormScheme scheme() throws StormSchemeException {
        return mSchemeLazy.get();
    }

    @Override
    public StormConverter<T> converter() throws StormConverterException {
        return mConverterLazy.get();
    }

    @Override
    public StormMetadata<T> metadata() {
        return mMetadataLazy.get();
    }
}
