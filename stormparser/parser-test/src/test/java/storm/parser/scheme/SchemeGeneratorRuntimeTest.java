package storm.parser.scheme;

import storm.parser.ParserAssert;
import storm.parser.StormParserException;
import storm.parser.StormParserFactory;
import storm.parser.TestReflectionInstanceCreator;

/**
 * Created by Dimitry Ivanov on 12.12.2015.
 */
public class SchemeGeneratorRuntimeTest extends SchemeGeneratorBaseTest {

    public StormScheme getScheme(Class<?> cl) {

        ParserAssert.assertNoApt(cl, StormSchemeAptNameBuilder.getInstance());

        try {
            return new StormParserFactory(new TestReflectionInstanceCreator()).provide(cl).scheme();
        } catch (StormParserException e) {
            throw new RuntimeException(e);
        }
    }
}
