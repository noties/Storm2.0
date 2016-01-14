package storm.parser;

import org.junit.Assert;

/**
 * Created by Dimitry Ivanov on 14.01.2016.
 */
public class ParserAssert {

    public static void assertNoApt(Class<?> cl, StormParserAptClassNameBuilder builder) {
        Assert.assertTrue(!apt(cl, builder));
    }

    public static void assertApt(Class<?> cl, StormParserAptClassNameBuilder builder) {
        Assert.assertTrue(apt(cl, builder));
    }

    private static boolean apt(Class<?> cl, StormParserAptClassNameBuilder builder) {
        final StormParserItemFactoryBase base = new StormParserItemFactoryBase(builder) {
            @Override
            public StormParserItem runtime(StormParserTable stormParserTable) throws StormParserException {
                return null;
            }
        };

        //noinspection unchecked
        return base.apt(cl) != null;
    }
}
