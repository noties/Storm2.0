package storm.parser;

import storm.parser.converter.StormConverter;
import storm.parser.converter.StormConverterException;
import storm.parser.metadata.StormMetadata;
import storm.parser.scheme.StormScheme;
import storm.parser.scheme.StormSchemeException;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public interface StormParser<T> {

    StormScheme scheme() throws StormParserException;
    StormConverter<T> converter() throws StormParserException;
    StormMetadata<T> metadata();

}
