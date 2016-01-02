package storm.parser;

import java.lang.reflect.Field;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public interface StormParserItemFactory<I extends StormParserItem> {

    <T> T apt(Class<?> cl);
    <T> I runtime(StormParserTable<Class<?>, Field, Class<?>> table) throws StormParserException;
}
