package storm.parser;

import storm.parser.converter.StormConverter;
import storm.parser.metadata.StormMetadata;
import storm.parser.scheme.StormScheme;

/**
 * Created by Dimitry Ivanov on 24.01.2016.
 */
class StormParserImpl<T> implements StormParser<T> {

    private final ParserLazy<StormScheme> mScheme;
    private final ParserLazy<StormConverter<T>> mConverter;
    private final ParserLazy<StormMetadata<T>> mMetadata;

    StormParserImpl(
            ParserLazy<StormScheme> scheme,
            ParserLazy<StormConverter<T>> converter,
            ParserLazy<StormMetadata<T>> metadata
    ) {
        mScheme = scheme;
        mConverter = converter;
        mMetadata = metadata;
    }

    @Override
    public StormScheme scheme() throws StormParserException {
        return mScheme.get();
    }

    @Override
    public StormConverter<T> converter() throws StormParserException {
        return mConverter.get();
    }

    @Override
    public StormMetadata<T> metadata() throws StormParserException {
        return mMetadata.get();
    }
}
