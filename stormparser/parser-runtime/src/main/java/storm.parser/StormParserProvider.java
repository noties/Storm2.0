package storm.parser;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public interface StormParserProvider {

    <T> StormParser<T> provideParser(Class<T> cl) throws StormParserException;

}
