package storm.parser.converter;

import storm.parser.ParserAssert;
import storm.parser.StormParserException;
import storm.parser.StormParserFactory;
import storm.parser.TestReflectionInstanceCreator;

/**
 * Created by Dimitry Ivanov on 14.01.2016.
 */
public class StormConverterAptTest extends StormConverterBaseTest {
    @Override
    <T> StormConverter<T> getConverter(Class<T> cl) {

        ParserAssert.assertApt(cl, StormConverterAptClassNameBuilder.getInstance());

        try {
            return new StormParserFactory(new TestReflectionInstanceCreator())
                    .provide(cl).converter();
        } catch (StormParserException e) {
            throw new RuntimeException(e);
        }
    }
}
