package storm.parser.columns;

import storm.parser.StormParserAptClassNameBuilderBase;

/**
 * Created by Dimitry Ivanov on 26.01.2016.
 */
public class StormColumnsAptNameBuilder extends StormParserAptClassNameBuilderBase {

    private static final StormColumnsAptNameBuilder INSTANCE = new StormColumnsAptNameBuilder();

    public static StormColumnsAptNameBuilder getInstance() {
        return INSTANCE;
    }

    private StormColumnsAptNameBuilder() {
        super("Columns");
    }
}
