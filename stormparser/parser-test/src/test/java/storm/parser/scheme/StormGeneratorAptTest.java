package storm.parser.scheme;

import storm.parser.ParserAssert;
import storm.parser.StormParserFactory;
import storm.parser.TestReflectionInstanceCreator;

/**
 * Created by Dimitry Ivanov on 22.12.2015.
 */
public class StormGeneratorAptTest extends SchemeGeneratorBaseTest {

    @Override
    public StormScheme getScheme(Class<?> cl) {

        ParserAssert.assertApt(cl, StormSchemeAptNameBuilder.getInstance());

        try {
            return new StormParserFactory(new TestReflectionInstanceCreator()).provide(cl).scheme();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
