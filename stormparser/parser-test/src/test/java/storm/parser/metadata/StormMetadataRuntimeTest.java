package storm.parser.metadata;

import storm.parser.ParserAssert;
import storm.parser.StormParserException;
import storm.parser.StormParserFactory;
import storm.parser.TestReflectionInstanceCreator;

/**
 * Created by Dimitry Ivanov on 15.01.2016.
 */
public class StormMetadataRuntimeTest extends StormMetadataBaseTest {

    @Override
    <T> StormMetadata<T> getMetadata(Class<T> cl) {

        ParserAssert.assertNoApt(cl, StormMetadataAptClassNameBuilder.getInstance());

        try {
            return new StormParserFactory(new TestReflectionInstanceCreator())
                    .provide(cl).metadata();
        } catch (StormParserException e) {
            throw new RuntimeException(e);
        }
    }
}
