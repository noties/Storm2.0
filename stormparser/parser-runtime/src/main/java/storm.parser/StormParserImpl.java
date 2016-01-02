package storm.parser;

import storm.parser.converter.StormConverter;
import storm.parser.metadata.StormMetadata;
import storm.parser.scheme.StormScheme;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
class StormParserImpl<T> implements StormParser<T> {

    private final ParserLazy<StormScheme, StormParserException> mSchemeLazy;
    private final ParserLazy<StormConverter<T>, StormParserException> mConverterLazy;
    private final ParserLazy<StormMetadata<T>, StormParserException> mMetadataLazy;

    StormParserImpl(
            ParserLazy<StormScheme, StormParserException> schemeLazy,
            ParserLazy<StormConverter<T>, StormParserException> converterLazy,
            ParserLazy<StormMetadata<T>, StormParserException> metadataLazy
    ) {
        mSchemeLazy = schemeLazy;
        mConverterLazy = converterLazy;
        mMetadataLazy = metadataLazy;
    }

    @Override
    public StormScheme scheme() throws StormParserException {
        return mSchemeLazy.get();
    }

    @Override
    public StormConverter<T> converter() throws StormParserException {
        return mConverterLazy.get();
    }

    @Override
    public StormMetadata<T> metadata() {
        try {
            return mMetadataLazy.get();
        } catch (StormParserException e) {
            throw new RuntimeException("Unexpected throw of StormParserException", e);
        }
    }
}
