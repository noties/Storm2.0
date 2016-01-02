package storm.parser;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public interface StormParserItemProvider<I extends StormParserItem> {

    <T> T apt(Class<?> cl);
    <T> I runtime(StormParserTable table) throws StormParserException;
}
