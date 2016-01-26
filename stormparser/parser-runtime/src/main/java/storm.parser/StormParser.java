package storm.parser;

import storm.parser.converter.StormConverter;
import storm.parser.metadata.StormMetadata;
import storm.parser.scheme.StormScheme;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public interface StormParser<T> {

    StormScheme         scheme()    throws StormParserException;
    StormConverter<T>   converter() throws StormParserException;
    StormMetadata<T>    metadata()  throws StormParserException;

}
