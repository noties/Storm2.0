package storm.parser.scheme;

import java.lang.reflect.Field;

import storm.parser.StormParserException;
import storm.parser.StormParserItemFactoryBase;
import storm.parser.StormParserTable;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public class StormSchemeFactory extends StormParserItemFactoryBase<StormScheme> {

    public StormSchemeFactory() {
        super(StormSchemeAptNameBuilder.getInstance());
    }

    @Override
    public <T> StormScheme runtime(StormParserTable<Class<?>, Field, Class<?>> table) throws StormParserException {
        return new StormSchemeRuntime(table);
    }
}
