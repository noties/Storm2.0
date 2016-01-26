package storm.parser;

import org.junit.Assert;

/**
 * Created by Dimitry Ivanov on 14.01.2016.
 */
public class ParserAssert {

    public static void assertApt(Class<?> cl, StormParserAptClassNameBuilder builder, Object... args) {
        Assert.assertTrue(apt(cl, builder));
    }

    private static boolean apt(Class<?> cl, StormParserAptClassNameBuilder builder, Object... args) {

        final StormParserItemFactory factory = new StormParserItemFactory();
        return factory.provide(builder, cl, args) != null;
    }
}
