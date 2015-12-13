package storm.parser;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class StormParserProviderRuntime implements StormParserProvider {

    @Override
    public <T> StormParser<T> provideParser(Class<T> cl) throws StormParserException {
        return null;
    }
}
