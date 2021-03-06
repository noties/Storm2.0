package storm.parser.metadata;

import storm.parser.StormParserAptClassNameBuilderBase;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public class StormMetadataAptClassNameBuilder extends StormParserAptClassNameBuilderBase {

    private static final StormMetadataAptClassNameBuilder INSTANCE = new StormMetadataAptClassNameBuilder();

    public static StormMetadataAptClassNameBuilder getInstance() {
        return INSTANCE;
    }

    private StormMetadataAptClassNameBuilder() {
        super("Metadata");
    }
}
