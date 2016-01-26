package storm.parser.scheme;

import storm.parser.StormParserAptClassNameBuilderBase;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public class StormSchemeAptNameBuilder extends StormParserAptClassNameBuilderBase {

    private static final StormSchemeAptNameBuilder INSTANCE = new StormSchemeAptNameBuilder();

    public static StormSchemeAptNameBuilder getInstance() {
        return INSTANCE;
    }

    private StormSchemeAptNameBuilder() {
        super("Scheme");
    }
}
